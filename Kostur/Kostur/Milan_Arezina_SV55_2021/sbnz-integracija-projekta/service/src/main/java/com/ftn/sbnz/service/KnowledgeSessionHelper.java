package com.ftn.sbnz.service;


import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class KnowledgeSessionHelper {

    public static KieContainer createRuleBase() {
        KieServices ks = KieServices.Factory.get();
        return ks.getKieClasspathContainer();
    }

    public static KieSession getStatefulKnowledgeSession(KieContainer kc, String sessionName) {
        return kc.newKieSession(sessionName);
    }
}
