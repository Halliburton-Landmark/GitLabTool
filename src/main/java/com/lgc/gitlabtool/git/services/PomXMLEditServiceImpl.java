package com.lgc.gitlabtool.git.services;

import static com.ximpleware.VTDNav.WS_LEADING;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.ModifyException;
import com.ximpleware.NavException;
import com.ximpleware.TranscodeException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

public class PomXMLEditServiceImpl implements PomXmlEditService {

    private static final Logger logger = LogManager.getLogger(PomXmlEditService.class);

    @Override
    public boolean addRepository(String path, String id, String url, String layout) {
        try {

            VTDGen vg = new VTDGen();
            if (!vg.parseFile(path, true)) {
                return false;
            }

            VTDNav vn = vg.getNav();
            XMLModifier xm = new XMLModifier(vn);

            AutoPilot apRepository = new AutoPilot();
            apRepository.bind(vn);
            apRepository.selectXPath("/project/repositories");
            if ((apRepository.evalXPath()) == -1) {
                return false;
            }

            if (vn.toElement(VTDNav.LAST_CHILD)) {
                System.out.println(vn.toString());
                xm.insertAfterElement(getRepoXmlString(id, url, layout));

                xm.output(path);
                return true;
            }

        } catch (NavException | XPathEvalException | IOException | ModifyException | XPathParseException | TranscodeException e) {
            logger.error("Error during adding repository to pom.xml " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean removeRepository(String path, String id) {
        try {

            VTDGen vg = new VTDGen();
            if (!vg.parseFile(path, true)) {
                return false;
            }

            VTDNav vn = vg.getNav();
            XMLModifier xm = new XMLModifier(vn);

            AutoPilot apRepository = new AutoPilot();
            apRepository.bind(vn);
            apRepository.selectXPath("/project/repositories/repository[id = '" + id + "']");
            if (apRepository.evalXPath() == -1) {
                return false;
            }

            long elementFragment = vn.getElementFragment();
            xm.remove(vn.expandWhiteSpaces(elementFragment, WS_LEADING));

            xm.output(path);
            return true;

        } catch (NavException | XPathParseException | XPathEvalException | IOException | ModifyException | TranscodeException e) {
            logger.error("Error during removing repository to pom.xml " + e.getMessage());
        }

        return false;
    }

    @Override
    public boolean modifyRepository(String path, String oldId, String newId, String newUrl, String newLayout) {
        try {

            VTDGen vg = new VTDGen();
            if (!vg.parseFile(path, true)) {
                return false;
            }

            VTDNav vn = vg.getNav();
            XMLModifier xm = new XMLModifier(vn);

            AutoPilot apRepository = new AutoPilot();
            apRepository.bind(vn);
            apRepository.selectXPath("/project/repositories/repository[id = '" + oldId + "']");
            if (apRepository.evalXPath() == -1) {
                return false;
            }

            editXmlNode(path, vn, xm, "id", newId);
            editXmlNode(path, vn, xm, "url", newUrl);
            editXmlNode(path, vn, xm, "layout", newLayout);

            return true;

        } catch (NavException | XPathEvalException | IOException | ModifyException | TranscodeException | XPathParseException e) {
            logger.error("Error during editing repository to pom.xml " + e.getMessage());
        }

        return false;
    }

    private void editXmlNode(String path, VTDNav vn, XMLModifier xm, String nodeKey, String nodeValue)
            throws NavException, ModifyException, IOException, TranscodeException {
        int index;
        if (vn.toElement(VTDNav.FIRST_CHILD, nodeKey)) {
            index = vn.getText();
            if (index != -1) {
                xm.updateToken(index, nodeValue);
                xm.output(path);
            }
            vn.toElement(VTDNav.PARENT);
        }
    }

    private static String getRepoXmlString(String id, String url, String layout) {
        return "\n        <repository>\n" +
                "            <id>" + id + "</id>\n" +
                "            <url>" + url + "</url>\n" +
                "            <layout>" + layout + "</layout>\n" +
                "        </repository>";
    }

}
