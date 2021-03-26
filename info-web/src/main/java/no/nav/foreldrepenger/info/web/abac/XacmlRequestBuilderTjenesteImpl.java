package no.nav.foreldrepenger.info.web.abac;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_FNR;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.SUBJECT_LEVEL;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.SUBJECT_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_SUBJECT_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.pdp.XacmlRequestBuilderTjeneste;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlAttributeSet;
import no.nav.vedtak.sikkerhet.pdp.xacml.XacmlRequestBuilder;
import no.nav.vedtak.util.Tuple;

@Dependent
@Alternative
@Priority(2)
public class XacmlRequestBuilderTjenesteImpl implements XacmlRequestBuilderTjeneste {
    private static final Logger LOG = LoggerFactory.getLogger(XacmlRequestBuilderTjenesteImpl.class);

    @Override
    public XacmlRequestBuilder lagXacmlRequestBuilder(PdpRequest pdpRequest) {
        XacmlRequestBuilder xacmlBuilder = new XacmlRequestBuilder();

        XacmlAttributeSet actionAttributeSet = new XacmlAttributeSet();
        actionAttributeSet.addAttribute(XACML10_ACTION_ACTION_ID, pdpRequest.getString(XACML10_ACTION_ACTION_ID));
        xacmlBuilder.addActionAttributeSet(actionAttributeSet);

        var identer = hentIdenter(pdpRequest, RESOURCE_FELLES_PERSON_FNR,
                RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE);

        if (identer.isEmpty()) {
            xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, null));
        } else {
            for (var ident : identer) {
                xacmlBuilder.addResourceAttributeSet(byggRessursAttributter(pdpRequest, ident));
            }
        }

        populerSubjects(pdpRequest, xacmlBuilder);

        return xacmlBuilder;
    }

    private void populerSubjects(PdpRequest pdpRequest, XacmlRequestBuilder xacmlBuilder) {
        var attrs = new XacmlAttributeSet();
        var found = false;

        if (pdpRequest.get(XACML10_SUBJECT_ID) != null) {
            attrs.addAttribute(XACML10_SUBJECT_ID, pdpRequest.getString(XACML10_SUBJECT_ID));
            found = true;
        }

        var level = pdpRequest.getString(SUBJECT_LEVEL);
        if (level != null) {
            String l = level.substring(level.length() - 1);
            attrs.addAttribute(SUBJECT_LEVEL, l);
            found = true;
        }
        if (pdpRequest.get(SUBJECT_TYPE) != null) {
            attrs.addAttribute(SUBJECT_TYPE, pdpRequest.getString(SUBJECT_TYPE));
            found = true;
        }
        if (found) {
            LOG.trace("Legger til subject attributes");
            xacmlBuilder.addSubjectAttributeSet(attrs);
        } else {
            LOG.trace("Legger IKKE til subject attributes");
        }
    }

    private static XacmlAttributeSet byggRessursAttributter(PdpRequest pdpRequest, Tuple<String, String> ident) {
        var resourceAttributeSet = new XacmlAttributeSet();
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_DOMENE, pdpRequest.getString(RESOURCE_FELLES_DOMENE));
        resourceAttributeSet.addAttribute(RESOURCE_FELLES_RESOURCE_TYPE,
                pdpRequest.getString(RESOURCE_FELLES_RESOURCE_TYPE));
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest,
                AppAbacAttributtType.RESOURCE_FORELDREPENGER_ANNEN_PART);
        setOptionalValueinAttributeSet(resourceAttributeSet, pdpRequest,
                AppAbacAttributtType.RESOURCE_FORELDREPENGER_ALENEOMSORG);

        if (ident != null) {
            resourceAttributeSet.addAttribute(ident.getElement1(), ident.getElement2());
        }

        return resourceAttributeSet;
    }

    private static void setOptionalValueinAttributeSet(XacmlAttributeSet resourceAttributeSet, PdpRequest pdpRequest,
            String key) {
        pdpRequest.getOptional(key).ifPresent(s -> resourceAttributeSet.addAttribute(key, s));
    }

    private static List<Tuple<String, String>> hentIdenter(PdpRequest pdpRequest, String... identNøkler) {
        List<Tuple<String, String>> identer = new ArrayList<>();
        for (String key : identNøkler) {
            identer.addAll(pdpRequest.getListOfString(key).stream().map(it -> new Tuple<>(key, it))
                    .collect(Collectors.toList()));
        }
        return identer;
    }
}
