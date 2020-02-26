package gov.nist.csd.pm.epp.functions;

import gov.nist.csd.pm.epp.FunctionEvaluator;
import gov.nist.csd.pm.epp.events.EventContext;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.pdp.PDP;
import gov.nist.csd.pm.pip.graph.Graph;
import gov.nist.csd.pm.pip.graph.model.nodes.Node;
import gov.nist.csd.pm.pip.graph.model.nodes.NodeType;
import gov.nist.csd.pm.pip.obligations.model.functions.Arg;
import gov.nist.csd.pm.pip.obligations.model.functions.Function;

import java.util.*;

public class CreateNodeExecutor implements FunctionExecutor {
    @Override
    public String getFunctionName() {
        return "create_node";
    }

    /**
     * parent name, parent type, parent properties, name, type, properties
     * @return
     */
    @Override
    public int numParams() {
        return 6;
    }

    @Override
    public Node exec(EventContext eventCtx, long userID, long processID, PDP pdp, Function function, FunctionEvaluator functionEvaluator) throws PMException {
        List<Arg> args = function.getArgs();

        // first arg is the name, can be function that returns a string
        Arg parentNameArg = args.get(0);
        String parentName = parentNameArg.getValue();
        if(parentNameArg.getFunction() != null) {
            parentName = functionEvaluator.evalString(eventCtx, userID, processID, pdp, parentNameArg.getFunction());
        }

        // second arg is the type, can be function
        Arg parentTypeArg = args.get(1);
        String parentType = parentTypeArg.getValue();
        if(parentTypeArg.getFunction() != null) {
            parentType = functionEvaluator.evalString(eventCtx, userID, processID, pdp, parentTypeArg.getFunction());
        }

        // third arg is the properties which is a map that has to come from a function
        /*Map<String, String> parentProps = new HashMap<>();
        if(args.size() > 2) {
            Arg propsArg = args.get(2);
            if (propsArg.getFunction() != null) {
                parentProps = (Map) functionEvaluator.evalMap(eventCtx, userID, processID, pdp, propsArg.getFunction());
            }
        }*/

        // fourth arg is the name, can be function
        Arg nameArg = args.get(2);
        String name = nameArg.getValue();
        if(nameArg.getFunction() != null) {
            name = functionEvaluator.evalString(eventCtx, userID, processID, pdp, nameArg.getFunction());
        }

        // fifth arg is the type, can be function
        Arg typeArg = args.get(3);
        String type = typeArg.getValue();
        if(typeArg.getFunction() != null) {
            type = functionEvaluator.evalString(eventCtx, userID, processID, pdp, typeArg.getFunction());
        }

        // sixth arg is the properties which is a map that has to come from a function
        Map<String, String> props = new HashMap<>();
        if(args.size() > 3) {
            Arg propsArg = args.get(4);
            if (propsArg.getFunction() != null) {
                props = (Map) functionEvaluator.evalMap(eventCtx, userID, processID, pdp, propsArg.getFunction());
            }
        }

        long id = new Random().nextLong();
        Graph graph = pdp.getPAP().getGraphPAP();

        Set<Node> search = graph.search(parentName, NodeType.toNodeType(parentType), new HashMap<>());
        if (search.isEmpty()) {
            throw new PMException(String.format("parent node %s with type %s and properties %s does not exist", parentName, parentType, new HashMap<>()));
        }
        Node parentNode = search.iterator().next();

        return graph.createNode(id, name, NodeType.toNodeType(type), props, parentNode.getID());
    }
}