package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.messenger.browser.Browser;

public class OmniGramSettingsActivity extends BaseFragment {
    private static final String GITHUB_URL = "https://github.com/wasssly/OmniGram";
    private static final String RELEASES_URL = "https://github.com/wasssly/OmniGram/releases";
    private static final int REQUEST_EXPORT_SETTINGS = 4101;
    private static final int REQUEST_IMPORT_SETTINGS = 4102;

    public static final int CATEGORY_GENERAL = 1;
    public static final int CATEGORY_APPEARANCE = 2;
    public static final int CATEGORY_CHATS = 3;
    public static final int CATEGORY_MEDIA = 4;
    public static final int CATEGORY_PRIVACY = 5;
    public static final int CATEGORY_DATA = 6;
    public static final int CATEGORY_PROJECT = 7;

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(true);
        actionBar.setTitle(LocaleController.getString(R.string.OmniGramSettingsTitle));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                if (id == -1) {
                    finishFragment();
                }
            }
        });

        LinearLayout content = createContent(context);
        addSection(context, content, R.string.OmniGramAppearanceSection,
                new int[] {R.string.OmniGramCategoryAppearance, R.string.OmniGramCategoryChats, R.string.OmniGramCategoryMedia},
                new int[] {R.drawable.settings_features, R.drawable.settings_chat, R.drawable.settings_data},
                new int[] {CATEGORY_APPEARANCE, CATEGORY_CHATS, CATEGORY_MEDIA});
        addSection(context, content, R.string.OmniGramGeneralSection,
                new int[] {R.string.OmniGramCategoryGeneral},
                new int[] {R.drawable.settings_power},
                new int[] {CATEGORY_GENERAL});
        addSection(context, content, R.string.OmniGramOtherSection,
                new int[] {R.string.OmniGramCategoryPrivacy},
                new int[] {R.drawable.settings_privacy},
                new int[] {CATEGORY_PRIVACY});
        addSection(context, content, R.string.OmniGramMoreSection,
                new int[] {R.string.OmniGramCategoryData, R.string.OmniGramCategoryProject},
                new int[] {R.drawable.settings_folders, R.drawable.settings_features},
                new int[] {CATEGORY_DATA, CATEGORY_PROJECT});

        TextInfoPrivacyCell footer = new TextInfoPrivacyCell(context);
        footer.setText(LocaleController.getString(R.string.OmniGramSettingsFooter));
        content.addView(footer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        fragmentView = wrapContent(context, content);
        return fragmentView;
    }

    private LinearLayout createContent(Context context) {
        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        boolean material3 = MessagesController.getGlobalMainSettings().getBoolean("omnigram_material3", true);
        int inset = material3 ? 12 : 0;
        content.setPadding(AndroidUtilities.dp(inset), AndroidUtilities.dp(inset), AndroidUtilities.dp(inset), AndroidUtilities.dp(16));
        return content;
    }

    private ScrollView wrapContent(Context context, LinearLayout content) {
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.addView(content, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
        return scrollView;
    }

    private void addSection(Context context, LinearLayout content, int sectionTitle, int[] titles, int[] icons, int[] categories) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        boolean material3 = MessagesController.getGlobalMainSettings().getBoolean("omnigram_material3", true);
        card.setBackground(createRoundedBackground(context, material3 ? 20 : 4));
        TextView section = new TextView(context);
        section.setText(LocaleController.getString(sectionTitle));
        section.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        section.setTextSize(16);
        section.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        section.setGravity(Gravity.CENTER_VERTICAL);
        section.setPadding(AndroidUtilities.dp(24), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(6));
        card.addView(section, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(42)));
        for (int i = 0; i < titles.length; i++) {
            addCategory(context, card, titles[i], icons[i], categories[i], i < titles.length - 1);
        }
        LinearLayout.LayoutParams cardParams = LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, AndroidUtilities.dp(12));
        content.addView(card, cardParams);
    }

    private GradientDrawable createRoundedBackground(Context context, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        drawable.setCornerRadius(AndroidUtilities.dp(radius));
        return drawable;
    }

    private void addCategory(Context context, LinearLayout content, int title, int icon, int category, boolean divider) {
        TextCell cell = new TextCell(context, 23, false, true, null);
        cell.setBackgroundColor(Color.TRANSPARENT);
        cell.setTextAndIcon(LocaleController.getString(title), icon, divider);
        cell.setOnClickListener(v -> presentFragment(new OmniGramCategoryActivity(category)));
        content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(62)));
    }

    public static class OmniGramCategoryActivity extends BaseFragment {
        private final int category;
        private final SharedPreferences preferences = MessagesController.getGlobalMainSettings();

        public OmniGramCategoryActivity(int category) {
            this.category = category;
        }

        @Override
        public View createView(Context context) {
            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setAllowOverlayTitle(true);
            actionBar.setTitle(getCategoryTitle());
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) finishFragment();
                }
            });

            LinearLayout content = new LinearLayout(context);
            content.setOrientation(LinearLayout.VERTICAL);
            content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            addSettings(context, content);
            ScrollView scrollView = new ScrollView(context);
            scrollView.setFillViewport(true);
            scrollView.addView(content, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
            fragmentView = scrollView;
            return fragmentView;
        }

        private String getCategoryTitle() {
            int id;
            switch (category) {
                case CATEGORY_APPEARANCE: id = R.string.OmniGramCategoryAppearance; break;
                case CATEGORY_CHATS: id = R.string.OmniGramCategoryChats; break;
                case CATEGORY_MEDIA: id = R.string.OmniGramCategoryMedia; break;
                case CATEGORY_PRIVACY: id = R.string.OmniGramCategoryPrivacy; break;
                case CATEGORY_DATA: id = R.string.OmniGramCategoryData; break;
                case CATEGORY_PROJECT: id = R.string.OmniGramCategoryProject; break;
                default: id = R.string.OmniGramCategoryGeneral;
            }
            return LocaleController.getString(id);
        }

        private void addSettings(Context context, LinearLayout content) {
            switch (category) {
                case CATEGORY_GENERAL:
                    addHeader(context, content, R.string.OmniGramGeneralSection);
                    addToggle(context, content, R.string.OmniGramSmoothAnimations, R.string.OmniGramSmoothAnimationsInfo, "view_animations", true, value -> SharedConfig.setAnimationsEnabled(value));
                    break;
                case CATEGORY_APPEARANCE:
                    addHeader(context, content, R.string.OmniGramAppearanceSection);
                    addToggle(context, content, R.string.OmniGramMaterial3, R.string.OmniGramMaterial3Info, "omnigram_material3", true, value -> {
                        finishFragment();
                        presentFragment(new OmniGramSettingsActivity());
                    });
                    addChoice(context, content, R.string.OmniGramCornerRadius, "omnigram_corner_radius", new String[] {"Малые", "Средние", "Большие"}, new int[] {11, 17, 24}, 17);
                    break;
                case CATEGORY_CHATS:
                    addHeader(context, content, R.string.OmniGramChatsSection);
                    addToggle(context, content, R.string.OmniGramFloatingTabs, R.string.OmniGramFloatingTabsInfo, "omnigram_floating_tabs", false, null);
                    addChoice(context, content, R.string.OmniGramInputStyle, "omnigram_input_style", new String[] {"Стандартная", "Компактная", "Плавающая"}, new String[] {"standard", "compact", "floating"}, "standard");
                    addToggle(context, content, R.string.OmniGramReorderMessageActions, R.string.OmniGramReorderMessageActionsInfo, "omnigram_reorder_message_actions", false, null);
                    addToggle(context, content, R.string.OmniGramHideMessageActions, R.string.OmniGramHideMessageActionsInfo, "omnigram_hide_message_actions", false, null);
                    break;
                case CATEGORY_MEDIA:
                    addHeader(context, content, R.string.OmniGramMediaSection);
                    addToggle(context, content, R.string.OmniGramAutoplayVideo, R.string.OmniGramAutoplayVideoInfo, "autoplay_video", true, null);
                    addToggle(context, content, R.string.OmniGramAutoplayGif, R.string.OmniGramAutoplayGifInfo, "autoplay_gif", true, null);
                    break;
                case CATEGORY_PRIVACY:
                    addHeader(context, content, R.string.OmniGramPrivacySection);
                    addToggle(context, content, R.string.OmniGramHidePhoneLocal, R.string.OmniGramHidePhoneLocalInfo, "omnigram_hide_phone_local", false, null);
                    addToggle(context, content, R.string.OmniGramSpoilerPhone, R.string.OmniGramSpoilerPhoneInfo, "omnigram_spoiler_phone", false, null);
                    addToggle(context, content, R.string.OmniGramCleanTracking, R.string.OmniGramCleanTrackingInfo, "omnigram_clean_tracking_params", true, null);
                    addToggle(context, content, R.string.OmniGramDisableProxyOnVpn, R.string.OmniGramDisableProxyOnVpnInfo, "omnigram_disable_proxy_on_vpn", false, null);
                    addToggle(context, content, R.string.OmniGramDisableAds, R.string.OmniGramDisableAdsInfo, "omnigram_disable_ads", false, null);
                    break;
                case CATEGORY_DATA:
                    addHeader(context, content, R.string.OmniGramDataSection);
                    addAction(context, content, R.string.OmniGramExportSettings, R.string.OmniGramExportSettingsInfo, v -> exportSettings());
                    addAction(context, content, R.string.OmniGramImportSettings, R.string.OmniGramImportSettingsInfo, v -> importSettings());
                    addAction(context, content, R.string.OmniGramResetSettings, R.string.OmniGramResetSettingsInfo, v -> confirmResetSettings());
                    break;
                case CATEGORY_PROJECT:
                    addHeader(context, content, R.string.OmniGramProjectSection);
                    addAction(context, content, R.string.OmniGramGitHub, null, v -> Browser.openUrl(getContext(), GITHUB_URL));
                    addAction(context, content, R.string.OmniGramReleases, null, v -> Browser.openUrl(getContext(), RELEASES_URL));
                    break;
            }
        }

        private String exportPayload;
        private JSONObject pendingImportedValues;

        private void confirmResetSettings() {
            if (getParentActivity() == null) return;
            new AlertDialog.Builder(getParentActivity())
                    .setTitle(LocaleController.getString(R.string.OmniGramResetSettings))
                    .setMessage(LocaleController.getString(R.string.OmniGramResetSettingsConfirm))
                    .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                    .setPositiveButton(LocaleController.getString(R.string.Reset), (dialog, which) -> {
                        preferences.edit()
                                .remove("view_animations")
                                .remove("autoplay_video")
                                .remove("autoplay_gif")
                                .remove("omnigram_hide_phone_local")
                                .remove("omnigram_spoiler_phone")
                                .remove("omnigram_clean_tracking_params")
                                .remove("omnigram_disable_proxy_on_vpn")
                                .remove("omnigram_disable_ads")
                                .remove("omnigram_material3")
                                .remove("omnigram_corner_radius")
                                .remove("omnigram_floating_tabs")
                                .remove("omnigram_input_style")
                                .remove("omnigram_reorder_message_actions")
                                .remove("omnigram_hide_message_actions")
                                .apply();
                        SharedConfig.bubbleRadius = 17;
                        SharedConfig.saveConfig();
                        SharedConfig.setAnimationsEnabled(true);
                        showSettingsToast(R.string.OmniGramSettingsReset);
                    }).show();
        }

        private void exportSettings() {
            try {
                JSONObject json = new JSONObject();
                json.put("schemaVersion", 1);
                JSONObject values = new JSONObject();
                values.put("view_animations", preferences.getBoolean("view_animations", true));
                values.put("autoplay_video", preferences.getBoolean("autoplay_video", true));
                values.put("autoplay_gif", preferences.getBoolean("autoplay_gif", true));
                values.put("omnigram_hide_phone_local", preferences.getBoolean("omnigram_hide_phone_local", false));
                values.put("omnigram_spoiler_phone", preferences.getBoolean("omnigram_spoiler_phone", false));
                values.put("omnigram_clean_tracking_params", preferences.getBoolean("omnigram_clean_tracking_params", true));
                values.put("omnigram_disable_proxy_on_vpn", preferences.getBoolean("omnigram_disable_proxy_on_vpn", false));
                values.put("omnigram_disable_ads", preferences.getBoolean("omnigram_disable_ads", false));
                values.put("omnigram_material3", preferences.getBoolean("omnigram_material3", true));
                values.put("omnigram_corner_radius", preferences.getInt("omnigram_corner_radius", 17));
                values.put("omnigram_floating_tabs", preferences.getBoolean("omnigram_floating_tabs", false));
                values.put("omnigram_input_style", preferences.getString("omnigram_input_style", "standard"));
                values.put("omnigram_reorder_message_actions", preferences.getBoolean("omnigram_reorder_message_actions", false));
                values.put("omnigram_hide_message_actions", preferences.getBoolean("omnigram_hide_message_actions", false));
                json.put("values", values);
                exportPayload = json.toString(2);
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, "omnigram-settings.json");
                getParentActivity().startActivityForResult(intent, REQUEST_EXPORT_SETTINGS);
            } catch (Exception e) {
                showSettingsToast(R.string.OmniGramSettingsOperationFailed);
            }
        }

        private void importSettings() {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            getParentActivity().startActivityForResult(intent, REQUEST_IMPORT_SETTINGS);
        }

        @Override
        public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
            super.onActivityResultFragment(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) return;
            try {
                if (requestCode == REQUEST_EXPORT_SETTINGS && exportPayload != null) {
                    java.io.OutputStream output = getParentActivity().getContentResolver().openOutputStream(data.getData());
                    if (output == null) throw new IllegalStateException("No output stream");
                    output.write(exportPayload.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    output.close();
                    showSettingsToast(R.string.OmniGramSettingsExported);
                } else if (requestCode == REQUEST_IMPORT_SETTINGS) {
                    java.io.InputStream input = getParentActivity().getContentResolver().openInputStream(data.getData());
                    if (input == null) throw new IllegalStateException("No input stream");
                    java.util.Scanner scanner = new java.util.Scanner(input, java.nio.charset.StandardCharsets.UTF_8.name()).useDelimiter("\\A");
                    String raw = scanner.hasNext() ? scanner.next() : "";
                    scanner.close();
                    JSONObject root = new JSONObject(raw);
                    if (root.optInt("schemaVersion", -1) != 1) throw new IllegalArgumentException("Unsupported schema");
                    pendingImportedValues = root.getJSONObject("values");
                    StringBuilder preview = new StringBuilder(LocaleController.getString(R.string.OmniGramImportPreview));
                    java.util.Iterator<String> keys = pendingImportedValues.keys();
                    while (keys.hasNext()) preview.append("\n• ").append(keys.next());
                    new AlertDialog.Builder(getParentActivity())
                            .setTitle(LocaleController.getString(R.string.OmniGramImportSettings))
                            .setMessage(preview.toString())
                            .setNegativeButton(LocaleController.getString(R.string.Cancel), null)
                            .setPositiveButton(LocaleController.getString(R.string.OK), (dialog, which) -> applyImportedSettings())
                            .show();
                }
            } catch (Exception e) {
                showSettingsToast(R.string.OmniGramSettingsOperationFailed);
            }
        }

        private void showSettingsToast(int message) {
            if (getParentActivity() != null) Toast.makeText(getParentActivity(), LocaleController.getString(message), Toast.LENGTH_SHORT).show();
        }

        private void applyImportedSettings() {
            if (pendingImportedValues == null) return;
            try {
                SharedPreferences.Editor editor = preferences.edit();
                if (pendingImportedValues.has("view_animations")) {
                    boolean enabled = pendingImportedValues.getBoolean("view_animations");
                    editor.putBoolean("view_animations", enabled);
                    SharedConfig.setAnimationsEnabled(enabled);
                }
                if (pendingImportedValues.has("autoplay_video")) editor.putBoolean("autoplay_video", pendingImportedValues.getBoolean("autoplay_video"));
                if (pendingImportedValues.has("autoplay_gif")) editor.putBoolean("autoplay_gif", pendingImportedValues.getBoolean("autoplay_gif"));
                if (pendingImportedValues.has("omnigram_hide_phone_local")) editor.putBoolean("omnigram_hide_phone_local", pendingImportedValues.getBoolean("omnigram_hide_phone_local"));
                if (pendingImportedValues.has("omnigram_spoiler_phone")) editor.putBoolean("omnigram_spoiler_phone", pendingImportedValues.getBoolean("omnigram_spoiler_phone"));
                if (pendingImportedValues.has("omnigram_clean_tracking_params")) editor.putBoolean("omnigram_clean_tracking_params", pendingImportedValues.getBoolean("omnigram_clean_tracking_params"));
                if (pendingImportedValues.has("omnigram_disable_proxy_on_vpn")) editor.putBoolean("omnigram_disable_proxy_on_vpn", pendingImportedValues.getBoolean("omnigram_disable_proxy_on_vpn"));
                if (pendingImportedValues.has("omnigram_disable_ads")) editor.putBoolean("omnigram_disable_ads", pendingImportedValues.getBoolean("omnigram_disable_ads"));
                if (pendingImportedValues.has("omnigram_material3")) editor.putBoolean("omnigram_material3", pendingImportedValues.getBoolean("omnigram_material3"));
                if (pendingImportedValues.has("omnigram_floating_tabs")) editor.putBoolean("omnigram_floating_tabs", pendingImportedValues.getBoolean("omnigram_floating_tabs"));
                if (pendingImportedValues.has("omnigram_input_style")) editor.putString("omnigram_input_style", pendingImportedValues.getString("omnigram_input_style"));
                if (pendingImportedValues.has("omnigram_reorder_message_actions")) editor.putBoolean("omnigram_reorder_message_actions", pendingImportedValues.getBoolean("omnigram_reorder_message_actions"));
                if (pendingImportedValues.has("omnigram_hide_message_actions")) editor.putBoolean("omnigram_hide_message_actions", pendingImportedValues.getBoolean("omnigram_hide_message_actions"));
                if (pendingImportedValues.has("omnigram_corner_radius")) {
                    int radius = pendingImportedValues.getInt("omnigram_corner_radius");
                    editor.putInt("omnigram_corner_radius", radius);
                    SharedConfig.bubbleRadius = radius;
                    SharedConfig.saveConfig();
                }
                editor.apply();
                pendingImportedValues = null;
                showSettingsToast(R.string.OmniGramSettingsImported);
            } catch (Exception e) {
                showSettingsToast(R.string.OmniGramSettingsOperationFailed);
            }
        }

        private void addHeader(Context context, LinearLayout content, int title) {
            HeaderCell header = new HeaderCell(context);
            header.setText(LocaleController.getString(title));
            content.addView(header, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
        }

        private void addToggle(Context context, LinearLayout content, int title, int info, String key, boolean defaultValue, ToggleChanged changed) {
            TextCheckCell cell = new TextCheckCell(context);
            cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            cell.setTextAndCheck(LocaleController.getString(title), preferences.getBoolean(key, defaultValue), true);
            cell.setOnClickListener(v -> {
                boolean enabled = !cell.isChecked();
                cell.setChecked(enabled);
                preferences.edit().putBoolean(key, enabled).apply();
                if (changed != null) changed.onChanged(enabled);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_ALL);
            });
            content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));
            if (info != 0) {
                TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(context);
                infoCell.setText(LocaleController.getString(info));
                content.addView(infoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }
        }

        private void addChoice(Context context, LinearLayout content, int title, String key, String[] labels, int[] values, int defaultValue) {
            TextCell cell = new TextCell(context, 23, false, true, null);
            cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            int selected = preferences.getInt(key, defaultValue);
            cell.setTextAndValue(LocaleController.getString(title), getChoiceLabel(selected, values, labels), true);
            cell.setOnClickListener(v -> new AlertDialog.Builder(getParentActivity())
                    .setTitle(LocaleController.getString(title))
                    .setItems(labels, (dialog, which) -> {
                        int radius = values[which];
                        preferences.edit().putInt(key, radius).apply();
                        SharedConfig.bubbleRadius = radius;
                        SharedConfig.saveConfig();
                        cell.setTextAndValue(LocaleController.getString(title), labels[which], true);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.updateInterfaces, MessagesController.UPDATE_MASK_ALL);
                    }).show());
            content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));
        }

        private String getChoiceLabel(int selected, int[] values, String[] labels) {
            for (int i = 0; i < values.length; i++) {
                if (values[i] == selected) return labels[i];
            }
            return labels[1];
        }

        private void addChoice(Context context, LinearLayout content, int title, String key, String[] labels, String[] values, String defaultValue) {
            TextCell cell = new TextCell(context, 23, false, true, null);
            cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            String selected = preferences.getString(key, defaultValue);
            cell.setTextAndValue(LocaleController.getString(title), getChoiceLabel(selected, values, labels), true);
            cell.setOnClickListener(v -> new AlertDialog.Builder(getParentActivity())
                    .setTitle(LocaleController.getString(title))
                    .setItems(labels, (dialog, which) -> {
                        preferences.edit().putString(key, values[which]).apply();
                        cell.setTextAndValue(LocaleController.getString(title), labels[which], true);
                    }).show());
            content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));
        }

        private String getChoiceLabel(String selected, String[] values, String[] labels) {
            for (int i = 0; i < values.length; i++) {
                if (values[i].equals(selected)) return labels[i];
            }
            return labels[0];
        }

        private void addAction(Context context, LinearLayout content, int title, Integer info, View.OnClickListener listener) {
            TextCell cell = new TextCell(context, 23, false, true, null);
            cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            cell.setText(LocaleController.getString(title), true);
            cell.setOnClickListener(listener);
            content.addView(cell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));
            if (info != null) {
                TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(context);
                infoCell.setText(LocaleController.getString(info));
                content.addView(infoCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));
            }
        }

        private interface ToggleChanged {
            void onChanged(boolean value);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }
}
