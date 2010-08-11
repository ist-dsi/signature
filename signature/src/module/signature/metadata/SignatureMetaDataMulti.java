package module.signature.metadata;

import java.util.ArrayList;
import java.util.List;

import module.signature.domain.SignatureIntention;
import module.signature.util.exporter.SignatureExporter;

public class SignatureMetaDataMulti extends SignatureMetaData<List<SignatureIntention>> {

    private List<SignatureMetaData> list;

    public SignatureMetaDataMulti(List<SignatureIntention> list) {
	super(list);
    }

    @Override
    protected void transverse(List<SignatureIntention> signIntentions) {
	list = new ArrayList<SignatureMetaData>();

	for (SignatureIntention si : signIntentions) {
	    list.add(si.getMetaData());
	}
    }

    @Override
    public void accept(SignatureExporter signatureExporter) {
	for (SignatureMetaData metaData : list) {
	    metaData.accept(signatureExporter);
	}
    }

}
