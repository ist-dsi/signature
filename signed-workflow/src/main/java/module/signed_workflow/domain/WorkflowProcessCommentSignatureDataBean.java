/*
 * @(#)WorkflowProcessCommentSignatureDataBean.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: João Antunes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Digital Signature Workflow Integration Module.
 *
 *   The Digital Signature Workflow Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Digital Signature Workflow Integration Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Digital Signature Workflow Integration Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.signed_workflow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import module.workflow.domain.WorkflowProcessComment;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import pt.ist.bennu.core.domain.User;

/**
 * Class used to wrap a bean so that it can easily be serialized to
 * XML/XHTML/whatever for the purposes of generating the signature data of a
 * document to be signed that must contain the Comments
 * 
 * @author João Antunes
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
        this.dateTime =
                this.undisplayedDateTime.toString(DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss").withLocale(new Locale("pt")));

    }

    public static List<WorkflowProcessCommentSignatureDataBean> createCommentBeans(List<WorkflowProcessComment> comments) {
        if (comments == null || comments.isEmpty()) {
            return null;
        }
        ArrayList<WorkflowProcessCommentSignatureDataBean> commentsToReturn =
                new ArrayList<WorkflowProcessCommentSignatureDataBean>();
        for (WorkflowProcessComment comment : comments) {
            commentsToReturn.add(new WorkflowProcessCommentSignatureDataBean(comment));
        }
        return commentsToReturn;
    }

}
