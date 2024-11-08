package com.oberasoftware.home.rules.api.general;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;
import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.trigger.Trigger;

import java.util.List;

/**
 * @author Renze de Vries
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Rule {

    private String id;
    private String name;
    private List<Statement> statements;
    private List<Trigger> triggers;

    public Rule(String id, String name, Statement statement, List<Trigger> triggers) {
        this.id = id;
        this.name = name;
        this.statements = Lists.newArrayList(statement);
        this.triggers = triggers;

    }

    public Rule(String id, String name, List<Statement> statements, List<Trigger> triggers) {
        this.id = id;
        this.name = name;
        this.statements = statements;
        this.triggers = triggers;
    }

    public Rule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Statement> getBlocks() {
        return statements;
    }

    public void setBlocks(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", block=" + statements +
                ", triggers=" + triggers +
                '}';
    }
}
