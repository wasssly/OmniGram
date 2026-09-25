package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.messenger.browser.Browser;

public class OmniGramSettingsActivity extends BaseFragment {
    private static final String PREF_EXPERIMENTAL_FEATURES = "omnigram_experimental_features";
    private static final String GITHUB_URL = "https://github.com/wasssly/OmniGram";
    private static final String RELEASES_URL = "https://github.com/wasssly/OmniGram/releases";

    public static boolean experimentalFeaturesEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean(PREF_EXPERIMENTAL_FEATURES, false);
    }

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

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        scrollView.addView(content, LayoutHelper.createScroll(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
        fragmentView = scrollView;

        HeaderCell appHeader = new HeaderCell(context);
        appHeader.setText(LocaleController.getString(R.string.OmniGramSettingsSection));
        content.addView(appHeader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        TextCheckCell animationsCell = new TextCheckCell(context);
        animationsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        animationsCell.setTextAndCheck(LocaleController.getString(R.string.OmniGramSmoothAnimations), preferences.getBoolean("view_animations", true), true);
        animationsCell.setOnClickListener(v -> {
            boolean enabled = !animationsCell.isChecked();
            animationsCell.setChecked(enabled);
            preferences.edit().putBoolean("view_animations", enabled).apply();
        });
        content.addView(animationsCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));

        TextInfoPrivacyCell animationsInfo = new TextInfoPrivacyCell(context);
        animationsInfo.setText(LocaleController.getString(R.string.OmniGramSmoothAnimationsInfo));
        content.addView(animationsInfo, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextCheckCell experimentalCell = new TextCheckCell(context);
        experimentalCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        experimentalCell.setTextAndCheck(LocaleController.getString(R.string.OmniGramExperimentalFeatures), experimentalFeaturesEnabled(), true);
        experimentalCell.setOnClickListener(v -> {
            boolean enabled = !experimentalCell.isChecked();
            experimentalCell.setChecked(enabled);
            preferences.edit().putBoolean(PREF_EXPERIMENTAL_FEATURES, enabled).apply();
        });
        content.addView(experimentalCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));

        TextInfoPrivacyCell experimentalInfo = new TextInfoPrivacyCell(context);
        experimentalInfo.setText(LocaleController.getString(R.string.OmniGramExperimentalFeaturesInfo));
        content.addView(experimentalInfo, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        HeaderCell projectHeader = new HeaderCell(context);
        projectHeader.setText(LocaleController.getString(R.string.OmniGramProjectSection));
        content.addView(projectHeader, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        TextCell githubCell = new TextCell(context, 23, false, true, null);
        githubCell.setText(LocaleController.getString(R.string.OmniGramGitHub), true);
        githubCell.setOnClickListener(v -> Browser.openUrl(getContext(), GITHUB_URL));
        content.addView(githubCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));

        TextCell releasesCell = new TextCell(context, 23, false, true, null);
        releasesCell.setText(LocaleController.getString(R.string.OmniGramReleases), true);
        releasesCell.setOnClickListener(v -> Browser.openUrl(getContext(), RELEASES_URL));
        content.addView(releasesCell, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, AndroidUtilities.dp(50)));

        TextInfoPrivacyCell footer = new TextInfoPrivacyCell(context);
        footer.setText(LocaleController.getString(R.string.OmniGramSettingsFooter));
        content.addView(footer, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT));

        return fragmentView;
    }

    @Override
    public boolean onFragmentCreate() {
        return super.onFragmentCreate();
    }
}
