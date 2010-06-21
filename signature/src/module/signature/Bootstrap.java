package module.signature;

import javax.servlet.http.HttpServletRequest;

import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;

public class Bootstrap {
    static {
	RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {
	    public boolean shouldFilter(HttpServletRequest httpServletRequest) {
		return !(httpServletRequest.getRequestURI().endsWith("/signatureAction.do")
			&& httpServletRequest.getQueryString() != null && httpServletRequest.getQueryString().contains(
			"method=receiveSignature"));
	    }
	});

	RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {
	    public boolean shouldFilter(HttpServletRequest httpServletRequest) {
		return !(httpServletRequest.getRequestURI().endsWith("/signatureAction.do")
			&& httpServletRequest.getQueryString() != null && httpServletRequest.getQueryString().contains(
			"method=getLogsToSign"));
	    }
	});
    }
}
