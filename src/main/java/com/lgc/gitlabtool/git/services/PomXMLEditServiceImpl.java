package com.lgc.gitlabtool.git.services;

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

public class PomXMLEditServiceImpl {
    private static final Logger logger = LogManager.getLogger(PomXMLModel.class);

    private static ConsoleService _consoleService;
    private static StateService _stateService;

    public PomXMLEditServiceImpl(ConsoleService consoleService, StateService stateService) {
        _consoleService = consoleService;
        _stateService = stateService;
    }

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

            while ((apRepository.evalXPath()) != -1) {
                if (vn.toElement(VTDNav.LAST_CHILD)) {
                    xm.insertAfterElement(getRepoXmlString(id, url, layout));
                }
            }

            xm.output(path);
            return true;
        } catch (NavException | XPathEvalException | IOException | ModifyException | XPathParseException | TranscodeException e) {
            e.printStackTrace();
        }


        return false;
    }

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
            if (apRepository.evalXPath() != -1) {
                long elementFragment = vn.getElementFragment();
                xm.remove(vn.expandWhiteSpaces(elementFragment));

                xm.output(path);
                return true;
            }

        } catch (NavException | XPathParseException | XPathEvalException | IOException | ModifyException | TranscodeException e) {
            e.printStackTrace();
        }

        return false;
    }

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

            int index;
            while (apRepository.evalXPath() != -1) {
                if (vn.toElement(VTDNav.FIRST_CHILD, "id")) {
                    index = vn.getText();
                    if (index != -1) {
                        xm.updateToken(index, newId);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }

                if (vn.toElement(VTDNav.FIRST_CHILD, "url")) {
                    index = vn.getText();
                    if (index != -1) {
                        xm.updateToken(index, newUrl);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }

                if (vn.toElement(VTDNav.FIRST_CHILD, "layout")) {
                    index = vn.getText();
                    if (index != -1) {
                        xm.updateToken(index, newLayout);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }
            }

            return true;
        } catch (NavException | XPathEvalException | IOException | ModifyException | TranscodeException | XPathParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private static String getRepoXmlString(String id, String url, String layout) {
        return "\n        <repository>\n" +
                "            <id>" + id + "</id>\n" +
                "            <url>" + url + "</url>\n" +
                "            <layout>" + layout + "</layout>\n" +
                "        </repository>";
    }

}
