/**
 * 
 */
package module.signed_workflow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import module.workflow.domain.WorkflowProcessComment;
import myorg.domain.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Class used to wrap a bean so that it can easily be serialized to
 * XML/XHTML/whatever for the purposes of generating the signature data of a
 * document to be signed that must contain the Comments
 * 
 * @author João Antunes (joao.antunes@tagus.ist.utl.pt)
 * 
 */
@SuppressWarnings("unused")
public class WorkflowProcessCommentSignatureDataBean implements Serializable {

    /**
     * the default serial version of the file
     */
    private static final long serialVersionUID = 1L;

    private final String comment;
    private final User undisplayedUser;
    private final String user;
    private final DateTime undisplayedDateTime;
    private final String dateTime;

    public WorkflowProcessCommentSignatureDataBean(WorkflowProcessComment comment) {
	this.comment = comment.getComment();
	this.undisplayedUser = comment.getCommenter();
	this.user = this.undisplayedUser.getPerson().getName() + " (" + this.undisplayedUser.getUsername() + ")";
	this.undisplayedDateTime = comment.getDate();
	this.dateTime = this.undisplayedDateTime.toString(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss").withLocale(
		new Locale("pt")));

    }

    public static List<WorkflowProcessCommentSignatureDataBean> createCommentBeans(List<WorkflowProcessComment> comments) {
	if (comments == null || comments.isEmpty())
	    return null;
	ArrayList<WorkflowProcessCommentSignatureDataBean> commentsToReturn = new ArrayList<WorkflowProcessCommentSignatureDataBean>();
	for (WorkflowProcessComment comment : comments) {
	    commentsToReturn.add(new WorkflowProcessCommentSignatureDataBean(comment));
	}
	return commentsToReturn;
    }

}
