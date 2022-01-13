package com.paulbaker.album.feature.edit;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paulbaker.album.core.platform.ImageSaver;
import com.paulbaker.album.core.utils.Utils;
import com.paulbaker.album.data.models.MediaStoreImage;
import com.paulbaker.album.feature.edit.adapter.EditingToolsAdapter;
import com.paulbaker.album.feature.edit.adapter.FilterViewAdapter;
import com.paulbaker.album.feature.edit.tools.EmojiBSFragment;
import com.paulbaker.album.feature.edit.tools.PropertiesBSFragment;
import com.paulbaker.album.feature.edit.tools.ShapeBSFragment;
import com.paulbaker.album.feature.edit.tools.StickerBSFragment;
import com.paulbaker.album.feature.edit.tools.TextEditorDialogFragment;
import com.paulbaker.album.feature.viewmodel.EditViewModel;
import com.paulbaker.album.feature.viewmodel.HomeViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import album.R;
import album.databinding.FragmentEditBinding;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder;
import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class EditFragment extends Fragment implements
        EditingToolsAdapter.OnItemSelected,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        ShapeBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener,
        FilterListener {
    private static final String TAG = "TAG";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;

    private FragmentEditBinding binding;
    private HomeViewModel viewModel;
    private EditViewModel editViewModel;

    private MediaStoreImage photo = null;
    private EditingToolsAdapter mEditingToolsAdapter;
    private FilterViewAdapter mFilterViewAdapter;

    private PhotoEditor mPhotoEditor;
    private ShapeBuilder mShapeBuilder;
    private final ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private ShapeBSFragment mShapeBSFragment;
    private PropertiesBSFragment mPropertiesBSFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel();
        setupEditingToolAdapter();
        setupFiltersToolAdapter();
        setupListenerClick();
        setupTool();
        observerPhoto();
        prepareEditor();
        setCallBackForEditor();
        handleOnBackPress();
    }

    private void handleOnBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }


    private void setupListenerClick() {
        binding.imgUndo.setOnClickListener(this);
        binding.imgRedo.setOnClickListener(this);
        binding.imgSave.setOnClickListener(this);
        binding.imgClose.setOnClickListener(this);
        binding.imgShare.setOnClickListener(this);
        binding.imgGallery.setOnClickListener(this);
        binding.imgCamera.setOnClickListener(this);
    }

    private void setupTool() {
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mShapeBSFragment = new ShapeBSFragment();
        mPropertiesBSFragment = new PropertiesBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);
        mShapeBSFragment.setPropertiesChangeListener(this);
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        editViewModel = new ViewModelProvider(requireActivity()).get(EditViewModel.class);
    }


    private void setupEditingToolAdapter() {
        mEditingToolsAdapter = new EditingToolsAdapter(this);
        binding.rvConstraintTools.setAdapter(mEditingToolsAdapter);
        binding.rvConstraintTools.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupFiltersToolAdapter() {
        mFilterViewAdapter = new FilterViewAdapter(this);
        binding.rvFilterView.setAdapter(mFilterViewAdapter);
        binding.rvFilterView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void observerPhoto() {
        viewModel.photo.observe(getViewLifecycleOwner(), mediaStoreImage -> {
            photo = mediaStoreImage;
            setPhotoForEditor();
        });
    }

    private void setPhotoForEditor() {
        if (photo != null) {
            binding.photoEditorView.getSource().setImageURI(photo.getContentUri());
        }
    }

    private void prepareEditor() {
        mPhotoEditor = new PhotoEditor.Builder(requireContext(), binding.photoEditorView)
                .setPinchTextScalable(true)
                .setClipSourceImage(true)
//                .setDefaultTextTypeface(mTextRobotoTf)
//                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();
    }


    private void setCallBackForEditor() {
        mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
            @Override
            public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                TextEditorDialogFragment textEditorDialogFragment =
                        TextEditorDialogFragment.show((AppCompatActivity) requireActivity(), text, colorCode);
                textEditorDialogFragment.setOnTextEditorListener((inputText, newColorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(newColorCode);
                    mPhotoEditor.editText(rootView, inputText, styleBuilder);
                    binding.txtCurrentTool.setText(R.string.label_text);
                });
            }

            @Override
            public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
                Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
            }

            @Override
            public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
                Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
            }

            @Override
            public void onStartViewChangeListener(ViewType viewType) {
                Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
            }

            @Override
            public void onStopViewChangeListener(ViewType viewType) {
                Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
            }

            @Override
            public void onTouchSourceImage(MotionEvent event) {
                Log.d(TAG, "onTouchView() called with: event = [" + event + "]");
            }
        });
    }


    @Override
    public void onToolSelected(com.paulbaker.album.feature.edit.ToolType toolType) {
        switch (toolType) {
            case SHAPE:
                mPhotoEditor.setBrushDrawingMode(true);
                mShapeBuilder = new ShapeBuilder();
                mPhotoEditor.setShape(mShapeBuilder);
                binding.txtCurrentTool.setText(R.string.label_shape);
                showBottomSheetDialogFragment(mShapeBSFragment);
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity) requireActivity());
                textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);

                    mPhotoEditor.addText(inputText, styleBuilder);
                    binding.txtCurrentTool.setText(R.string.label_text);
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                binding.txtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case FILTER:
                binding.txtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                showBottomSheetDialogFragment(mEmojiBSFragment);
                break;
            case STICKER:
                showBottomSheetDialogFragment(mStickerBSFragment);
                break;
        }
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getChildFragmentManager(), fragment.getClass().getName());
    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(binding.getRoot());

        if (isVisible) {
            mConstraintSet.clear(binding.rvFilterView.getId(), ConstraintSet.START);
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(binding.rvFilterView.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(binding.rvFilterView.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(binding.getRoot(), changeBounds);

        mConstraintSet.applyTo(binding.getRoot());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                handleClose();
                break;
            case R.id.imgShare:
                shareImage();
                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }


    private void shareImage() {
        mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), saveBitmap, "Title", null);
                Uri imageUri = Uri.parse(path);
                share.putExtra(Intent.EXTRA_STREAM, imageUri);
                startActivity(Intent.createChooser(share, "Select"));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(),"Cannot share image!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleClose() {
        if (mIsFilterVisible) {
            showFilter(false);
            binding.txtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            Navigation.findNavController(binding.getRoot()).popBackStack();
        }
    }


    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Save", (dialog, which) -> saveImage());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Discard", (dialog, which) ->
                Navigation.findNavController(binding.getRoot()).popBackStack());
        builder.create().show();
    }

    private void saveImage() {
        mPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                try {
                    Uri uri = Utils.saveImage(requireContext(), saveBitmap, requireContext().getResources().getString(R.string.app_name) + Calendar.getInstance().getTime());
                    viewModel.setPhoto(new MediaStoreImage(
                            (long) saveBitmap.getGenerationId(),
                            requireContext().getResources().getString(R.string.app_name) + Calendar.getInstance().getTime(),
                            Calendar.getInstance().getTime(),
                            uri
                    ));
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    assert data != null;
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    binding.photoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        assert data != null;
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                        binding.photoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        binding.txtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeColor(colorCode));
        binding.txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeOpacity(opacity));
        binding.txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapeSizeChanged(int shapeSize) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(shapeSize));
        binding.txtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onShapePicked(ShapeType shapeType) {
        mPhotoEditor.setShape(mShapeBuilder.withShapeType(shapeType));
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        binding.txtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
