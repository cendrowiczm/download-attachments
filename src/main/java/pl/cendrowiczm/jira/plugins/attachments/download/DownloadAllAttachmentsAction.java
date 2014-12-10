package pl.cendrowiczm.jira.plugins.attachments.download;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.attachment.Attachment;
import com.atlassian.jira.util.io.InputStreamConsumer;
import com.atlassian.jira.web.action.issue.AbstractViewIssue;

import webwork.action.Action;
import webwork.action.ActionContext;

/**
 *
 * @author cendrowiczm@gmail.com
 * @since 1.0
 */
public class DownloadAllAttachmentsAction extends AbstractViewIssue {

	public DownloadAllAttachmentsAction(SubTaskManager subTaskManager) {
		super(subTaskManager);
	}

	@Override
	public String doExecute() throws Exception {
		MutableIssue issue = getIssueObject();
		
		SimpleDateFormat sourceDateFormat = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		
		final String zipName = issue.getKey() + "_allAttachments_" + sourceDateFormat.format(new Date()) + ".zip";

		Collection<Attachment> atts = issue.getAttachments();
		AttachmentManager am = ComponentAccessor.getAttachmentManager();

		final File file = File.createTempFile(zipName, ".zip");

		final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
		
		Map<String, Integer> existingNames = new HashMap<String, Integer>();
		for (Attachment att : atts) {
			String originalAttName = att.getFilename();
			String attName = originalAttName;
			int indexForAttName = 1;
			
			if (existingNames.containsKey(attName)) {
				indexForAttName = existingNames.get(attName) + 1;
				attName = attName + "-" + indexForAttName;
			}
			
			existingNames.put(originalAttName, indexForAttName);
			
			ZipEntry newEntry = new ZipEntry(attName);
			zos.putNextEntry(newEntry);
			am.streamAttachmentContent(att, new InputStreamConsumer<Void>() {
				@Override
				public Void withInputStream(InputStream inint) throws IOException {
					try {
						BufferedInputStream bis = new BufferedInputStream(inint);
						while (bis.available() > 0) {
							zos.write(bis.read());
						}
						bis.close();
						zos.closeEntry();
						//int read = 0;
						//byte[] bytes = new byte[1024];
						//while ((read = inint.read(bytes)) != -1) {
						//	dest.write(bytes, 0, read);
						//}
					} finally {
						inint.close();
						//dest.flush();
						//dest.close();
					}
					return null;
				}

			});
			//break;
		}
		
		zos.close();
		
		HttpServletResponse response = ActionContext.getResponse();
		response.reset();

		//File exportFile = File.createTempFile("SchedulerBackup_", ".xml");
		Long length = file.length();

		response.setHeader("Content-length", Long.toString(length));
		response.setHeader("Content-type", "application/zip");
		response.setHeader("Content-disposition", "attachment;filename=" + zipName);

		byte[] backupBuff = new byte[(int) file.length()];
		FileInputStream fileInputStream = new FileInputStream(file);
		fileInputStream.read(backupBuff);
		response.getOutputStream().write(backupBuff);

		fileInputStream.close();
		file.delete();
		return Action.NONE;
	}

	@Override
	public String doDefault() throws Exception {

		return Action.INPUT;
	}

}
