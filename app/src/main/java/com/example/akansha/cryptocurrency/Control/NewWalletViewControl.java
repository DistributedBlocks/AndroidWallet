package com.example.akansha.cryptocurrency.Control;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.akansha.cryptocurrency.Database.DatabaseHelper;
import com.example.akansha.cryptocurrency.Preferences.SolarBankerPreferenceManager;
import com.example.akansha.cryptocurrency.R;
import com.example.akansha.cryptocurrency.Utils.AndroidAppUtils;
import com.example.akansha.cryptocurrency.View.SetPinActivity;

import mobile.Mobile;

/**
 * Class to control  new wallet creation
 */
public class NewWalletViewControl implements View.OnClickListener {

    private String TAG = NewWalletViewControl.class.getSimpleName();
    private AppCompatActivity mActivity;
    private View new_wallet_slider_background, load_wallet_slider_background;
    private TextView new_label, load_label;
    private TextView wallet_name_heading, wallet_seed_heading, wallet_confirm_seed_heading;
    private EditText wallet_name_text, wallet_seed_text, wallet_seed_confirm;
    private String seed_value = "";
    private Button createButton;
    private String confirm_seed_value = "";
    private boolean isNewWalletSelected = true;
    private String walletName = "unnamedWallet";

    private Dialog walletSeedAlertDialog, safeGuardDialog;
    private TextView alert_ok_button, continue_button;
    private CheckBox safeGuardCheckbox;
    private TextView dialogTitle, safeGuardDialogMessage;

    private DatabaseHelper databaseHelper;
    private boolean isSeedReminderChecked = false;

    public NewWalletViewControl(AppCompatActivity mActivity) {
        this.mActivity = mActivity;
        initViews();
        initClickListener();
    }

    /**
     * Initialise click listener on views
     */
    private void initClickListener() {

        new_label.setOnClickListener(this);
        load_label.setOnClickListener(this);
        createButton.setOnClickListener(this);
        setWalletSeedTextListener();
        setConfirmSeedTextListener();


    }

    private void setConfirmSeedTextListener() {

        wallet_seed_confirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                AndroidAppUtils.showLog(TAG, "confirm seed text changed: " + wallet_seed_confirm.getText());
                confirm_seed_value = wallet_seed_confirm.getText().toString();
                if (confirm_seed_value.equals(seed_value))
                    createButton.setEnabled(true);
                else
                    createButton.setEnabled(false);

            }
        });

    }

    private void setWalletSeedTextListener() {

        wallet_seed_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                AndroidAppUtils.showLog(TAG, "seed text changed: " + wallet_seed_text.getText());
                seed_value = wallet_seed_text.getText().toString();

            }
        });

    }

    /**
     * Initialise GUI elements
     */
    private void initViews() {

        if (mActivity != null) {

            new_wallet_slider_background = mActivity.findViewById(R.id.new_slider_background);
            load_wallet_slider_background = mActivity.findViewById(R.id.load_slider_background);
            new_label = mActivity.findViewById(R.id.new_label);
            load_label = mActivity.findViewById(R.id.load_label);
            wallet_confirm_seed_heading = mActivity.findViewById(R.id.wallet_confirm_seed_heading);
            wallet_name_text = mActivity.findViewById(R.id.wallet_name_text);

            wallet_seed_text = mActivity.findViewById(R.id.wallet_seed_text);
            wallet_seed_text.setRawInputType(InputType.TYPE_CLASS_TEXT);
            wallet_seed_text.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            wallet_seed_confirm = mActivity.findViewById(R.id.wallet_seed_confirm);
            wallet_seed_confirm.setRawInputType(InputType.TYPE_CLASS_TEXT);
            wallet_seed_confirm.setImeOptions(EditorInfo.IME_ACTION_DONE);


            createButton = mActivity.findViewById(R.id.create_button);
            wallet_name_text = mActivity.findViewById(R.id.wallet_name_text);

            databaseHelper = new DatabaseHelper(mActivity);
            setSeedValue();


        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

    }

    /**
     * Set seed value
     */
    private void setSeedValue() {

        try {

            seed_value = Mobile.newWordSeed();
            wallet_seed_text.setText(seed_value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.new_label:

                showNewWalletView();

                break;
            case R.id.load_label:

                showLoadWalletGUI();

                break;
            case R.id.create_button:

                showSafeGuardDialog();


                break;
            case R.id.seed_alert_ok_button:

                if (walletSeedAlertDialog != null) {
                    walletSeedAlertDialog.dismiss();
                    walletSeedAlertDialog.cancel();
                } else
                    AndroidAppUtils.showErrorLog(TAG, "walletSeedAlertDialog is null");

                break;

            case R.id.continue_button:

                if (safeGuardDialog != null) {
                    if (isSeedReminderChecked) {

                        safeGuardDialog.dismiss();
                        safeGuardDialog.cancel();
                        createNewWallet();

                    } else
                        AndroidAppUtils.showErrorLog(TAG, "safeGuardDialog is not checked");
                } else
                    AndroidAppUtils.showErrorLog(TAG, "safeGuardDialog is null");

                break;
        }

    }

    /**
     * Show safeguard your seed dialog
     */
    private void showSafeGuardDialog() {


        if (mActivity != null) {
            safeGuardDialog = new Dialog(mActivity);
            safeGuardDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(safeGuardDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            safeGuardDialog.setContentView(R.layout.disclaimer_dialog);
            initDialogViews();
            initDialogViewsClickListener();
            safeGuardDialog.show();
            safeGuardDialog.getWindow().setAttributes(lWindowParams);


        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

    }

    /**
     * Initialise disclaimerDialog view click listener
     */
    private void initDialogViewsClickListener() {

        if (safeGuardCheckbox != null) {
            safeGuardCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {

                        isSeedReminderChecked = true;
                        continue_button.setEnabled(true);
                    } else {

                        isSeedReminderChecked = false;
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

        if (safeGuardDialog != null) {

            safeGuardCheckbox = safeGuardDialog.findViewById(R.id.checkbox);
            continue_button = safeGuardDialog.findViewById(R.id.continue_button);
            dialogTitle = safeGuardDialog.findViewById(R.id.title);
            safeGuardDialogMessage = safeGuardDialog.findViewById(R.id.message);

            dialogTitle.setText(mActivity.getResources().getString(R.string.seed_reminder_heading));
            dialogTitle.setTextColor(mActivity.getResources().getColor(R.color.reminder_red));

            safeGuardDialogMessage.setText(mActivity.getResources().getString(R.string.seed_reminder_message));
            safeGuardCheckbox.setText(mActivity.getResources().getString(R.string.seed_reminder_checkbox));

        } else
            AndroidAppUtils.showErrorLog(TAG, "disclaimer disclaimerDialog is null");

    }

    /**
     * Create New Wallet
     */
    private void createNewWallet() {

        if (!seed_value.isEmpty()) {

            walletName = wallet_name_text.getText().toString();
            if (walletName.trim().isEmpty())
                walletName = "unNamedWallet";

            try {


                new CreateNewWallet(mActivity).createNewWallet(seed_value, walletName);
                SolarBankerPreferenceManager.getInstance(mActivity).setisWalletCreated(true);
                mActivity.startActivity(new Intent(mActivity, SetPinActivity.class));
                mActivity.finish();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            AndroidAppUtils.showErrorLog(TAG, "seed value is empty");
            showWalletSeedAlert();
        }
    }

    /**
     * Show wallet alert seed
     */
    private void showWalletSeedAlert() {

        if (mActivity != null) {

            walletSeedAlertDialog = new Dialog(mActivity);
            walletSeedAlertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            walletSeedAlertDialog.setContentView(R.layout.seed_value_alert_dialog);

            WindowManager.LayoutParams lWindowParams = new WindowManager.LayoutParams();

            lWindowParams.copyFrom(walletSeedAlertDialog.getWindow().getAttributes());
            lWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            lWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            alert_ok_button = walletSeedAlertDialog.findViewById(R.id.seed_alert_ok_button);
            alert_ok_button.setOnClickListener(this);
            walletSeedAlertDialog.show();
            walletSeedAlertDialog.getWindow().setAttributes(lWindowParams);

        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is null");

    }

    /**
     * Show UI to to load wallet
     */
    private void showLoadWalletGUI() {

        if (mActivity != null) {

            isNewWalletSelected = false;
            new_label.setTextColor(mActivity.getResources().getColor(R.color.lightGreyText));
            new_wallet_slider_background.setVisibility(View.GONE);
            load_wallet_slider_background.setVisibility(View.VISIBLE);
            load_label.setTextColor(mActivity.getResources().getColor(R.color.white));
            wallet_confirm_seed_heading.setVisibility(View.GONE);
            wallet_seed_confirm.setVisibility(View.GONE);
            wallet_seed_text.setText("");
            createButton.setEnabled(true);


        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is  null");

    }

    /**
     * Show UI to to create new wallet
     */
    private void showNewWalletView() {

        if (mActivity != null) {

            isNewWalletSelected = true;
            new_label.setTextColor(mActivity.getResources().getColor(R.color.white));
            new_wallet_slider_background.setVisibility(View.VISIBLE);
            load_wallet_slider_background.setVisibility(View.GONE);
            load_label.setTextColor(mActivity.getResources().getColor(R.color.lightGreyText));
            wallet_confirm_seed_heading.setVisibility(View.VISIBLE);
            wallet_seed_confirm.setVisibility(View.VISIBLE);
            setSeedValue();
            wallet_seed_confirm.setText("");
            createButton.setEnabled(false);

        } else
            AndroidAppUtils.showErrorLog(TAG, "mActivity is  null");

    }
}

