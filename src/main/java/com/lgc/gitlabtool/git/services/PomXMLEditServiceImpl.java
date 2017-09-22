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

/**
 * Created by H185176 on 19.09.2017.
 */
public class PomXMLEditServiceImpl {
    private static final Logger logger = LogManager.getLogger(PomXMLModel.class);

    private static final String RELEASE_NAME_KEY = "releaseName";
    private static final String ECLIPSE_RELEASE_KEY = "eclipse.release";
    private static final String POM_NAME = "pom.xml";

    private static final String SUCCESSFUL_CHANGE_MESSAGE = "The pom.xml file was changed successfully.";
    private static final String CHANGE_ERROR_MESSAGE = "ERROR in changing the pom.xml file.";

    private static ConsoleService _consoleService;
    private static StateService _stateSerice;
    public static final String UNDEFINED_TEXT = "[Undefined]";

    public PomXMLEditServiceImpl(ConsoleService consoleService, StateService stateService) {
        _consoleService = consoleService;
        _stateService = stateService;
    }


    public boolean addRepository(String path, String id, String url, String layout) {
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

            int index = -1;
            while ((index = apRepository.evalXPath()) != -1) {
                if (vn.toElement(VTDNav.FIRST_CHILD, "id")) {
                    int j = vn.getText();
                    if (j != -1) {
                        System.out.println(" text node ==>" + vn.toString(j));
                        xm.updateToken(j, newId);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }

                if (vn.toElement(VTDNav.FIRST_CHILD, "url")) {
                    int j = vn.getText();
                    if (j != -1) {
                        System.out.println(" text node ==>" + vn.toString(j));
                        xm.updateToken(j, newUrl);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }

                if (vn.toElement(VTDNav.FIRST_CHILD, "layout")) {
                    int j = vn.getText();
                    if (j != -1) {
                        System.out.println(" text node ==>" + vn.toString(j));
                        xm.updateToken(j, newLayout);
                        xm.output(path);
                    }
                    vn.toElement(VTDNav.PARENT);
                }
            }


        } catch (NavException | XPathEvalException | IOException | ModifyException | TranscodeException | XPathParseException e) {
            e.printStackTrace();
        }
    }


}
