package com.rae.gongfutimer_v2.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.rae.gongfutimer_v2.R;

import java.util.HashMap;
import java.util.Locale;

/**
 * This class represents a dialog that allows the user to select and preview different preset colors
 * for each timer's icon. The user may also optionally manually input a hex value to set the color
 * of the icon if this setting was enabled.
 */
public class SaveIconDialogFragment extends DialogFragment
{
    /**
     * The listener for this fragment.
     * @see SaveIconDialogListener
     */
    private SaveIconDialogListener listener;

    // Icon selection fields
    /**
     * The integer identifier that indicates what the selected icon was. Initially, this is set to
     * the configuration's previously saved icon.
     */
    private int lastIcon;
    /**
     * The ImageView of the type 1 icon which changes color depending on the selected color.
     */
    private ImageView icon1Preview;
    /**
     * The ImageView of the type 2 icon which changes color depending on the selected color.
     */
    private ImageView icon2Preview;
    /**
     * The ImageView of the rectangle that encloses the icon1Preview. If the user has selected icon
     * type 1, this rectangle will be visible.
     */
    private ImageView icon1SelectView;
    /**
     * The ImageView of the rectangle that encloses the icon2Preview. If the user has selected icon
     * type 2, this rectangle will be visible.
     */
    private ImageView icon2SelectView;

    // Color selection fields
    /**
     * The integer corresponding to the selected icon color. Initially, this is set to the
     * configuration's previously saved color.
     */
    private int lastColor;
    /**
     * This ImageView will reference the selected icon color, which is indicated by a circle border.
     */
    private ImageView lastCircle;
    /**
     * This HashMap contains the preset colors and their corresponding ImageViews.
     */
    private HashMap<ImageView,Integer> paletteMap;

    // Hex input fields
    /**
     * This boolean indicates if the user has enabled hex input in the Settings.
     */
    private boolean showHexInput;
    /**
     * This EditText is where the user can enter a hex value to set as the color of the icon.
     */
    private EditText hexInputEditText;
    /**
     * This ImageView shows the hex value that the user has inputted, but if there is no input or if
     * the hex value is invalid, it will show as black.
     */
    private ImageView hexInputPreviewImage;

    /**
     * This initializes the fragment.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * This is called after the fragment is associated with the SaveConfigActivity. It will try to
     * set this class's listener.
     * @param context - The activity that the fragment is associated with.
     */
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        try {
            listener = (SaveIconDialogListener) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Must implement SaveIconDialogListener");
        }
    }

    /**
     * This creates and returns the view. The "save icon dialog" will be inflated, which has two
     * previews for color selection, some preset colors to choose from, and optionally a hex input
     * and its preview.
     * @param inflater - Inflates views in the fragment.
     * @param container - The parent view for the fragment UI to attach to.
     * @param savedInstanceState
     * @return - The fragment's view UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.save_icon_dialog, container, false);

        return view;
    }

    /**
     * This makes a new onClickListener for the icon, which indicates the icon type that has been
     * selected.
     * @return - A click listener for the icon selection ImageViews.
     */
    private View.OnClickListener makeIconClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (v instanceof ImageView)
                {
                    changeIconSelection((ImageView) v);
                }
            }
        };
    }

    /**
     * This draws a rectangle that indicates which of the icon types is selected.
     * @return - A rectangle drawable.
     */
    private GradientDrawable makeIconRectangle()
    {
        GradientDrawable rect = new GradientDrawable();
        rect.setShape(GradientDrawable.RECTANGLE);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.textPrimaryColor, typedValue, true);
        @ColorInt int strokeColor = typedValue.data;
        rect.setStroke(8, strokeColor);

        return rect;
    }

    /**
     * This sets up the UI of the Views, filling them with initial information.
     * @param view - The View from onCreateView.
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Icon selection
        icon1Preview = (ImageView) view.findViewById(R.id.save_icon_dialog_icon1_image_view);
        icon2Preview = (ImageView) view.findViewById(R.id.save_icon_dialog_icon2_image_view);
        icon1Preview.setOnClickListener(makeIconClickListener());
        icon2Preview.setOnClickListener(makeIconClickListener());
        icon1SelectView = (ImageView) view.findViewById(R.id.save_icon_dialog_icon1_select_view);
        icon2SelectView = (ImageView) view.findViewById(R.id.save_icon_dialog_icon2_select_view);
        icon1SelectView.setImageDrawable(makeIconRectangle());
        icon2SelectView.setImageDrawable(makeIconRectangle());
        try {
            int currentIcon = this.getArguments().getInt(getString(R.string.save_icon_name_key));
            int currentColor = this.getArguments().getInt(getString(R.string.save_icon_color_key));
            icon1Preview.setColorFilter(currentColor);
            icon2Preview.setColorFilter(currentColor);
            lastColor = currentColor;
            lastIcon = currentIcon;
        } catch (NullPointerException npe) {
            Log.e("SAVEICONDIALOGFRAG", "Bundle did not contain color data for instantiation");
            icon1Preview.setColorFilter(Color.BLACK);
            icon2Preview.setColorFilter(Color.BLACK);
        }

        if (lastIcon == R.drawable.ic_gongfutimerlogo01)
        {
            icon2SelectView.setVisibility(View.GONE);
        }
        else if (lastIcon == R.drawable.ic_timer_plain)
        {
            icon1SelectView.setVisibility(View.GONE);
        }

        // Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.save_icon_action);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (item.getItemId() == R.id.save_icon_apply_action)
                {
                    if (!(hexHasError()))
                    {
                        listener.onApplyIconClick(lastIcon, lastColor);
                        dismiss();
                    }
                    else
                    {
                        // TODO: want to highlight/catch attention?
                        Toast.makeText(getActivity(), "The hex value provided is invalid.", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });

        // Theme
        SharedPreferences sharedPref = getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String themeName = sharedPref.getString(getString(R.string.settings_theme_list_key), "");
        if (themeName.equals(getString(R.string.pref_theme_value_clay)) || themeName.equals(getString(R.string.pref_theme_value_dark)))
        {
            // needs to be light
            toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        }
        else
        {
            // needs to be dark
            toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        }

        // Hex input
        hexInputEditText = (EditText) view.findViewById(R.id.save_icon_dialog_hex_input_edit_view);
        showHexInput = sharedPref.getBoolean(getString(R.string.settings_hex_input_key), false);
        if (showHexInput)
        {
            ConstraintLayout hexInputLayout = (ConstraintLayout) view.findViewById(R.id.save_icon_hex_input_layout);
            hexInputLayout.setVisibility(View.VISIBLE);
            hexInputPreviewImage = view.findViewById(R.id.save_icon_dialog_hex_input_preview_image_view);
            hexInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count)
                {
                    // defaults to black
                    int color = Color.BLACK;
                    if (!(hexHasError()))
                    {
                        int numZeroesToAppend = 6 - s.length();
                        String colorString = s.toString();
                        if (numZeroesToAppend != 0)
                        {
                            StringBuilder stringBuilder = new StringBuilder(colorString);
                            for (int i = 0; i < numZeroesToAppend; ++i)
                            {
                                stringBuilder.append(0);
                            }
                            colorString = stringBuilder.toString();
                        }
                        color = Color.parseColor(String.format(Locale.US, "#%s", colorString));
                    }
                    changeColorSelection(hexInputPreviewImage, color);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
        else
        {
            ConstraintLayout hexInputLayout = (ConstraintLayout) view.findViewById(R.id.save_icon_hex_input_layout);
            hexInputLayout.setVisibility(View.GONE);
        }

        // Color preset selection
        paletteMap = new HashMap<>();
        setPalette(view);
    }

    /**
     * This sets up the preset color palette. Users can pick one of the preset colors to set the
     * icon to.
     * @param view
     */
    private void setPalette(View view)
    {
        int[] dullColors = {R.color.colorDullRedIcon, R.color.colorDullYellowIcon,
                R.color.colorDullTeaGreenIcon, R.color.colorDullGreenIcon,
                R.color.colorDullBlueIcon, R.color.colorDullPurpleIcon,
                R.color.colorDullPinkIcon, R.color.colorDullBrownIcon,
                R.color.colorDullGreyIcon};
        int[] paleColors = {R.color.colorPaleRedIcon, R.color.colorPaleYellowIcon,
                R.color.colorPaleTeaGreenIcon, R.color.colorPaleGreenIcon,
                R.color.colorPaleBlueIcon, R.color.colorPalePurpleIcon,
                R.color.colorPalePinkIcon, R.color.colorPaleBrownIcon,
                R.color.colorPaleGreyIcon};

        LinearLayout dullColorLayout = view.findViewById(R.id.save_icon_dialog_dull_layout);
        for (int color : dullColors)
        {
            ImageView colorView = makeColorView(color, view);
            paletteMap.put(colorView, color);
            dullColorLayout.addView(colorView);
        }
        LinearLayout paleColorLayout = view.findViewById(R.id.save_icon_dialog_pale_layout);
        for (int color : paleColors)
        {
            ImageView colorView = makeColorView(color, view);
            paletteMap.put(colorView, color);
            paleColorLayout.addView(colorView);
        }

        lastCircle = null;
    }

    /**
     * This creates the colored circles associated to preset colors in the {@link #paletteMap}.
     * @param color - The integer value associated with a preset color.
     * @param view
     * @return - The palette circle ImageView associated with a preset color.
     */
    private ImageView makeColorView(int color, View view)
    {
        ImageView paletteCircle = new ImageView(getContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int marginPixels = getResources().getDimensionPixelSize(R.dimen.padding_4);
        int size = (width / 10) - marginPixels;

        paletteCircle.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) paletteCircle.getLayoutParams();
        marginParams.setMargins(marginParams.leftMargin, marginParams.topMargin, marginPixels, 2*marginPixels);

        int fillColor = getResources().getColor(color);
        paletteCircle.setImageDrawable(makePaletteCircle(false, fillColor));

        paletteCircle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view instanceof ImageView)
                {
                    ImageView paletteCircle = (ImageView) view;
                    try {
                        int colorPicked = paletteMap.get(paletteCircle);
                        changeColorSelection(paletteCircle, getResources().getColor(colorPicked));
                    } catch (NullPointerException npe) {
                        Log.e("SAVEICONDIALOGFRAG", "Could not find associated color from map");
                    }
                }

            }
        });
        return paletteCircle;
    }

    /**
     * This creates a drawable oval that can be set into an ImageView.
     * @param hasStroke - Indicates if the circle should have a stroke surrounding the oval.
     * @param fillColor - The color of the oval.
     * @return - An oval drawable.
     */
    private GradientDrawable makePaletteCircle(boolean hasStroke, int fillColor)
    {
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(fillColor);

        if (hasStroke)
        {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getContext().getTheme();
            theme.resolveAttribute(R.attr.textPrimaryColor, typedValue, true);
            @ColorInt int strokeColor = typedValue.data;
            circle.setStroke(8, strokeColor);
        }

        return circle;
    }

    /**
     * This starts the dialog and sets up its dimensions.
     */
    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    /**
     * This checks if the hex string given by the user is a valid color.
     * @return - True if the hex value has an error, false if the hex value is valid.
     */
    private boolean hexHasError()
    {
        if (showHexInput)
        {
            // must check
            String hexString = hexInputEditText.getText().toString();
            if (hexString.matches("([a-f]|[A-F]|[0-9]){1,6}"))
            {
                // If < 6 chars, 0's are assumed to be appended to the end.
                Log.i("SAVEICONDIALOGFRAG", "Valid hex: " + hexString);
                return false; // does not have errors
            }
            else
            {
                Log.i("SAVEICONDIALOGFRAG", "Invalid hex: " + hexString);
                return true; // has errors
            }
        }
        else
        {
            // no hex input is needed. option is not enabled
            return false;
        }
    }

    /**
     * This updates the {@link #lastCircle} and {@link #lastColor} to the most recently selected
     * circle and colors.
     * @param currentCircle - The most recently selected circle.
     * @param currentColor - The color of the most recently selected circle.
     */
    private void changeColorSelection(ImageView currentCircle, int currentColor)
    {
        if (lastCircle != null)
        {
            // unselect
            lastCircle.setImageDrawable(makePaletteCircle(false, lastColor));
        }

        // select
        currentCircle.setImageDrawable(makePaletteCircle(true, currentColor));

        // update
        lastCircle = currentCircle;
        lastColor = currentColor;
        changeIconPreviews();
    }

    /**
     * This updates the {@link #lastIcon} and changes the UI to indicate which icon was selected by
     * showing the outline.
     * @param currentIcon - The ImageView of the icon type that was clicked by the user.
     */
    private void changeIconSelection(ImageView currentIcon)
    {
        if (lastIcon == R.drawable.ic_gongfutimerlogo01)
        {
            if (!(currentIcon.equals(icon1Preview)))
            {
                // unselect icon1
                icon1SelectView.setVisibility(View.GONE);
                // select icon2
                icon2SelectView.setVisibility(View.VISIBLE);
                lastIcon = R.drawable.ic_timer_plain;
            }
        }
        else if (lastIcon == R.drawable.ic_timer_plain)
        {
            if (!(currentIcon.equals(icon2Preview)))
            {
                // unselect icon2
                icon2SelectView.setVisibility(View.GONE);
                // select icon2
                icon1SelectView.setVisibility(View.VISIBLE);
                lastIcon = R.drawable.ic_gongfutimerlogo01;
            }
        }
    }

    /**
     * This changes the colors of the icon previews to match the selected color.
     */
    private void changeIconPreviews()
    {
        icon1Preview.setColorFilter(lastColor);
        icon2Preview.setColorFilter(lastColor);
    }

    /**
     * This listener is for the dialog when the Apply button is clicked so that changes can be made
     * to the timer configuration's icon.
     */
    public interface SaveIconDialogListener
    {
        /**
         * This is called when the user chooses to Apply changes.
         * @param iconId - The type of icon.
         * @param color - The color of the icon.
         */
        public void onApplyIconClick(int iconId, int color);
    }
}
