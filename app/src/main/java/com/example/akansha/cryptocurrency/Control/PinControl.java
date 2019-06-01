package com.example.akansha.cryptocurrency.Control;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.akansha.cryptocurrency.Application.SolarBankerApplication;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.Utils.GlobalConfig;
import com.example.akansha.cryptocurrency.View.NewWalletActivity;
import com.example.akansha.cryptocurrency.View.WalletContainerActivity;

/**
 * Class to control wallet pin
 */
public class PinControl implements View.OnClickListener {


    private String TAG = PinControl.class.getSimpleName();
    private AppCompatActivity mActivity;
    private ImageView dot_0, dot_1, dot_2, dot_3, dot_4, dot_5;
    private TextView button_1, button_2, button_3, button_4, button_5, button_6, button_7, button_8, button_9, button_0, button_del;

    private TextView alert_ok_button;
    private TextView pin_heading, pin_message;


    private CheckBox disclaimerCheckBox;
    private Button continue_button;
    private Dialog disclaimerDialog, alertDialog;
    private boolean isDisclaimerChecked = false;

    private SolarBankerPreferenceManager solarBankerPreferenceManager;


    private String walletPin = "";
    private String confirmWalletPin = "";

    private boolean isWalletPinSet = false;

    private int numberOfDigitInPin = 4;

    private Spinner pinTypeSelector;
    private boolean isWalletPinAlreadySet = false;

    private boolean isPerformTransaction;
    private boolean isChangePin;
    private boolean isFinishActivity;
    private boolean isGetSeedValue;

    public PinControl(AppCompatActivity mActivity, boolean isPerformTransaction, boolean isChangePin, boolean isGetSeedValue) {

        this.mActivity = mActivity;
        this.isPerformTransaction = isPerformTransaction;
        this.isChangePin = isChangePin;
        this.isGetSeedValue = isGetSeedValue;
        initViews();
        initClickListener();

    }

    /**
     * Show Wallet Pin GUI
     */
    public void showPinGUI() {

        if (solarBankerPreferenceManager.getIsWalletPinSet() || isPerformTransaction || isChangePin || isGetSeedValue) {

            isWalletPinSet = true;
            walletPin = solarBankerPreferenceManager.getWalletPin();
            numberOfDigitInPin = walletPin.length();
            isWalletPinAlreadySet = true;
            showConfirmPinGUI();

        } else {

            showDisclaimerDialog();

        }
    }


    /**
     * Show confirm pin GUI
     */
    private void showConfirmPinGUI() {

        resetAllWalletDots();
        pinTypeSelector.setVisibility(View.GONE);
        pin_heading.setText(mActivity.getResources().getString(R.string.confirm_pin));

        if (mActivity != null) {
            if (isGetSeedValue)
                pin_message.setText(mActivity.getResources().getString(R.string.wallet_show_seed));
            else if (isChangePin)
                pin_message.setText(mActivity.getResources().getString(R.string.confirm_pin_text_to_change_pin));
            else if (isPerformTransaction)
                pin_message.setText(mActivity.getResources().getString(R.string.pin_request_to_send));
            else if (isWalletPinAlreadySet)
                pin_message.setText(mActivity.getResources().getString(R.string.pin_heading_interrogate));
            else
                pin_message.setText(mActivity.getResources().getString(R.string.confirm_pin_text));
        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");


    }

    /**
     * Initialise GUI elements
     */
    private void initViews() {

        if (mActivity != null) {
            dot_0 = mActivity.findViewById(R.id.dot_0);
            dot_1 = mActivity.findViewById(R.id.dot_1);
            dot_2 = mActivity.findViewById(R.id.dot_2);
            dot_3 = mActivity.findViewById(R.id.dot_3);
            dot_4 = mActivity.findViewById(R.id.dot_4);
            dot_5 = mActivity.findViewById(R.id.dot_5);

            button_0 = mActivity.findViewById(R.id.button_0);
            button_1 = mActivity.findViewById(R.id.button_1);
            button_2 = mActivity.findViewById(R.id.button_2);
            button_3 = mActivity.findViewById(R.id.button_3);
            button_4 = mActivity.findViewById(R.id.button_4);
            button_5 = mActivity.findViewById(R.id.button_5);
            button_6 = mActivity.findViewById(R.id.button_6);
            button_7 = mActivity.findViewById(R.id.button_7);
            button_8 = mActivity.findViewById(R.id.button_8);
            button_9 = mActivity.findViewById(R.id.button_9);
            button_del = mActivity.findViewById(R.id.button_delete);

            pin_heading = mActivity.findViewById(R.id.pin_heading);
            pin_message = mActivity.findViewById(R.id.pin_message);

            pinTypeSelector = mActivity.findViewById(R.id.pin_type_selector);

            solarBankerPreferenceManager = SolarBankerPreferenceManager.getInstance(SolarBankerApplication.solarBankerApplicationContext);

        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");
    }


    /**
     * Initialise Click Listener
     */
    private void initClickListener() {

        button_0.setOnClickListener(this);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        button_7.setOnClickListener(this);
        button_8.setOnClickListener(this);
        button_9.setOnClickListener(this);
        button_del.setOnClickListener(this);


        pinTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                AndroidAppUtils.showInfoLog(TAG, "pin type  selected");
                String pinType = (String) adapterView.getItemAtPosition(i);

                if (pinType.equals(mActivity.getResources().getString(R.string.four_digit_pin))) {
                    AndroidAppUtils.showLog(TAG, "user selected 4 digit pin");
                    numberOfDigitInPin = 4;
                    walletPin = "";
                    setGUIForWalletPin();
                } else {
                    AndroidAppUtils.showLog(TAG, "user selected 6 digit pin");
                    walletPin = "";
                    numberOfDigitInPin = 6;
                    setGUIForWalletPin();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    /**
     * Set GUI to enter wallet pin
     */
    private void setGUIForWalletPin() {

        if (numberOfDigitInPin == 4) {

            resetAllWalletDots();
            dot_4.setVisibility(View.GONE);
            dot_5.setVisibility(View.GONE);

        } else {
            if (numberOfDigitInPin == 6) {

                resetAllWalletDots();
                dot_4.setVisibility(View.VISIBLE);
                dot_5.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * Reset all wallet dots to default
     */
    private void resetAllWalletDots() {

        dot_0.setImageResource(R.drawable.black_dot);
        dot_1.setImageResource(R.drawable.black_dot);
        dot_2.setImageResource(R.drawable.black_dot);
        dot_3.setImageResource(R.drawable.black_dot);
        dot_4.setImageResource(R.drawable.black_dot);
        dot_5.setImageResource(R.drawable.black_dot);

    }


    /**
     * Show disclaimer Dialog
     */
    public void showDisclaimerDialog() {

        if (mActivity != null) {
            disclaimerDialog = new Dialog(mActivity);

            disclaimerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            disclaimerDialog.setContentView(R.layout.disclaimer_dialog);
            initDialogViews();
            initDialogViewsClickListener();

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(disclaimerDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            disclaimerDialog.show();

            disclaimerDialog.getWindow().setAttributes(lWindowParams);


        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");


    }

    /**
     * Initialise disclaimerDialog view click listener
     */
    private void initDialogViewsClickListener() {

        if (disclaimerCheckBox != null) {
            disclaimerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {

                        isDisclaimerChecked = true;
                        continue_button.setEnabled(true);
                    } else {

                        isDisclaimerChecked = false;
                        continue_button.setEnabled(false);

                    }
                }
            });
        } else
            AndroidAppUtils.showErrorLog(TAG, "disclaimerCheckBox is  null");

        if (continue_button != null)
            continue_button.setOnClickListener(this);

    }

    /**
     * Initialise disclaimerDialog GUI elements
     */
    private void initDialogViews() {

        if (disclaimerDialog != null) {

            disclaimerCheckBox = disclaimerDialog.findViewById(R.id.checkbox);
            continue_button = disclaimerDialog.findViewById(R.id.continue_button);
        } else
            AndroidAppUtils.showErrorLog(TAG, "disclaimer disclaimerDialog is null");

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.continue_button:

                AndroidAppUtils.showInfoLog(TAG, "clicked continue buttom");
                if (isDisclaimerChecked) {
                    if (disclaimerDialog != null) {
                        disclaimerDialog.dismiss();
                        disclaimerDialog.cancel();
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "Dialog is null");
                } else
                    AndroidAppUtils.showErrorLog(TAG, "Disclaimer is unchecked");


                break;

            case R.id.button_0:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {

                    walletPin += "0";
                    setInputDotColor(walletPin.length() - 1, true);

                    setWalletPin();

                } else {
                    confirmWalletPin += "0";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }


                break;
            case R.id.button_1:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "1";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();

                } else {
                    confirmWalletPin += "1";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }

                break;
            case R.id.button_2:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "2";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "2";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_3:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "3";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "3";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_4:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "4";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "4";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_5:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "5";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "5";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_6:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "6";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "6";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_7:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "7";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "7";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_8:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "8";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "8";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_9:

                if (!isWalletPinSet && walletPin.length() < numberOfDigitInPin) {
                    walletPin += "9";
                    setInputDotColor(walletPin.length() - 1, true);
                    setWalletPin();
                } else {
                    confirmWalletPin += "9";
                    setInputDotColor(confirmWalletPin.length() - 1, true);
                    setConfirmWalletPin();
                }
                break;
            case R.id.button_delete:

                if (!isWalletPinSet) {
                    if (walletPin != null && !walletPin.isEmpty()) {
                        setInputDotColor(walletPin.length() - 1, false);
                        walletPin = walletPin.substring(0, walletPin.length() - 1);
                        AndroidAppUtils.showInfoLog(TAG, "Remaining wallet pin: " + walletPin);
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "wallet pin is  null or empty");
                } else {

                    if (confirmWalletPin != null && !confirmWalletPin.isEmpty()) {
                        setInputDotColor(confirmWalletPin.length() - 1, false);
                        confirmWalletPin = confirmWalletPin.substring(0, confirmWalletPin.length() - 1);
                        AndroidAppUtils.showInfoLog(TAG, "Remaining confirmWalletPin : " + confirmWalletPin);
                    } else
                        AndroidAppUtils.showErrorLog(TAG, "wallet pin is  null or empty");

                }

                break;

            case R.id.alert_ok_button:

                if (alertDialog != null) {

                    alertDialog.dismiss();
                    alertDialog.cancel();

                } else
                    AndroidAppUtils.showErrorLog(TAG, "alertDialog is null");
                break;
        }

    }

    /**
     * Ask user to set PIN
     */
    private void askUserToSetPin() {

        resetAllWalletDots();
        pinTypeSelector.setVisibility(View.VISIBLE);
        pin_heading.setText(mActivity.getResources().getString(R.string.pin_heading_choose));
        pin_message.setText(mActivity.getResources().getString(R.string.pin_choose_message));


    }

    /**
     * Set confirm wallet pin
     */
    private void setConfirmWalletPin() {

        if (confirmWalletPin.length() == numberOfDigitInPin) {
            if (confirmWalletPin.equals(walletPin)) {

                AndroidAppUtils.showLog(TAG, "Confirm wallet pin is equal to wallet pin");
                solarBankerPreferenceManager.setIsWalletPinSet(true);
                solarBankerPreferenceManager.setWalletPin(walletPin);


                if (isChangePin) {

                    isWalletPinAlreadySet = false;
                    isWalletPinSet = false;
                    askUserToSetPin();


                } else if (isPerformTransaction || isGetSeedValue) {
                    GlobalConfig.isWalletActionAuthenticated = true;
                    mActivity.finish();
                } else if (isFinishActivity)
                    mActivity.finish();
                else if (!solarBankerPreferenceManager.isWalletCreated())
                    openCreateWalletScreen();
                else if (isWalletPinAlreadySet)
                    openWalletContainerActivity();

            } else {

                AndroidAppUtils.showErrorLog(TAG, "confirm wallet pin mismatch: wallet pin: " + walletPin + " confirm wallet pin: " + confirmWalletPin);
                confirmWalletPin = "";

                if (!isWalletPinAlreadySet) {
                    walletPin = "";
                    isWalletPinSet = false;
                    askUserToSetPin();
                } else {
                    resetAllWalletDots();

                    showAlertDialog();
                }
            }
        } else
            AndroidAppUtils.showLog(TAG, "Confirm wallet pin is not equal to wallet pin: " + confirmWalletPin + " length: " + confirmWalletPin.length());
    }

    private void openWalletContainerActivity() {

        if (mActivity != null) {
            mActivity.startActivity(new Intent(mActivity, WalletContainerActivity.class));
            mActivity.finish();
        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");
    }

    /**
     * Open screen to create new wallet
     */
    private void openCreateWalletScreen() {

        if (mActivity != null) {
            mActivity.startActivity(new Intent(mActivity, NewWalletActivity.class));
            mActivity.finish();
        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

    }

    /**
     * Show Alert Dialog
     */
    private void showAlertDialog() {

        if (mActivity != null) {
            alertDialog = new Dialog(mActivity);

            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            alertDialog.setContentView(R.layout.alert_dialog);
            alert_ok_button = alertDialog.findViewById(R.id.alert_ok_button);
            alert_ok_button.setOnClickListener(this);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(alertDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            alertDialog.show();

            alertDialog.getWindow().setAttributes(lWindowParams);
        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");


    }

    /**
     * Set wallet pin
     */
    private void setWalletPin() {

        if (walletPin.length() == numberOfDigitInPin) {
            isWalletPinSet = true;

            if (isChangePin) {
                isChangePin = false;
                isFinishActivity = true;
                confirmWalletPin = "";
            }
            showConfirmPinGUI();
            AndroidAppUtils.showInfoLog(TAG, "COmplete wallet pin: " + walletPin);
        } else
            AndroidAppUtils.showInfoLog(TAG, "Length is not 5:  " + walletPin + " Length: " + walletPin.length());
    }

    /**
     * Set color of input dot
     *
     * @param walletPinLength
     */
    private void setInputDotColor(int walletPinLength, boolean isEnableDot) {

        switch (walletPinLength) {
            case 0:

                if (isEnableDot)
                    dot_0.setImageResource(R.drawable.orange_dot);
                else
                    dot_0.setImageResource(R.drawable.black_dot);

                break;
            case 1:

                if (isEnableDot)
                    dot_1.setImageResource(R.drawable.orange_dot);
                else
                    dot_1.setImageResource(R.drawable.black_dot);

                break;
            case 2:

                if (isEnableDot)
                    dot_2.setImageResource(R.drawable.orange_dot);
                else
                    dot_2.setImageResource(R.drawable.black_dot);

                break;
            case 3:

                if (isEnableDot)
                    dot_3.setImageResource(R.drawable.orange_dot);
                else
                    dot_3.setImageResource(R.drawable.black_dot);

                break;

            case 4:

                if (isEnableDot)
                    dot_4.setImageResource(R.drawable.orange_dot);
                else
                    dot_4.setImageResource(R.drawable.black_dot);

                break;

            case 5:

                if (isEnableDot)
                    dot_5.setImageResource(R.drawable.orange_dot);
                else
                    dot_5.setImageResource(R.drawable.black_dot);

                break;
        }
    }
}
