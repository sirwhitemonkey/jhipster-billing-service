package com.hawaiki.cucumber.stepdefs;

import com.hawaiki.BillingServiceApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = BillingServiceApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
