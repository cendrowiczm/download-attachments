package pl.cendrowiczm.jira.plugins.attachments.download;

import java.util.Map;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.api.license.entity.PluginLicense;

/**
 *
 * @author cendrowiczm@gmail.com
 * @since 1.0
 */
public class LicenseValidator implements Condition {
	
	final PluginLicenseManager pluginLicenseManager;
	
	public LicenseValidator(final PluginLicenseManager pluginLicenseManager) {
		this.pluginLicenseManager = pluginLicenseManager;
	}

	@Override
	public void init(Map<String, String> map) throws PluginParseException {
		// do nothing
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> map) {
		if (pluginLicenseManager.getLicense().isDefined()) {
			PluginLicense license = pluginLicenseManager.getLicense().get();
			if (license.getError().isDefined()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

}
