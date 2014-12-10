package pl.cendrowiczm.jira.plugins.attachments.download;

import java.util.Map;

import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;

/**
 *
 * @author cendrowiczm@gmail.com
 */
public class AttachmentsAmountValidator implements Condition {

	@Override
	public void init(Map<String, String> map) throws PluginParseException {
		// do nothing
	}

	@Override
	public boolean shouldDisplay(Map<String, Object> map) {
		try {
			IssueImpl issue = (IssueImpl) map.get("issue");
			if (issue != null) {
				return !issue.getAttachments().isEmpty();
			}
		} catch (Exception ex) {
			return false;
		}
		
		return false;
	}

}
