package module.signature.presentationTier.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.domain.VirtualHost;
import myorg.domain.contents.ActionNode;
import myorg.domain.contents.Node;
import myorg.domain.groups.UserGroup;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.servlets.functionalities.CreateNodeAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/signatureInterfaceCreationAction")
public class InterfaceCreationAction extends ContextBaseAction {

    @CreateNodeAction(bundle = "SIGNATURE_RESOURCES", key = "add.node.signatures.interface", groupKey = "label.module.signature")
    public final ActionForward createAnnouncmentNodes(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
	final Node node = getDomainObject(request, "parentOfNodesToManageId");

	final ActionNode homeNode = ActionNode.createActionNode(virtualHost, node, "/signature", "pendingSignatures",
		"resources.SignatureResources", "label.module.signature", UserGroup.getInstance());

	ActionNode.createActionNode(virtualHost, homeNode, "/signature", "pendingSignatures", "resources.SignatureResources",
		"link.sideBar.signature.pendingSignatures", UserGroup.getInstance());

	ActionNode.createActionNode(virtualHost, homeNode, "/signature", "concludedSignatures", "resources.SignatureResources",
		"link.sideBar.signature.concludedSignatures", UserGroup.getInstance());

	//	ActionNode.createActionNode(virtualHost, homeNode, "/signature", "configure", "resources.SignatureResources",
	//		"link.sideBar.signature.configure", Role.getRole(RoleType.MANAGER));

	return forwardToMuneConfiguration(request, virtualHost, node);
    }
}
