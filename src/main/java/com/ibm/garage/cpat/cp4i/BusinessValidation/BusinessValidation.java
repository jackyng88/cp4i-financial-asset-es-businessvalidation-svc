package com.ibm.garage.cpat.cp4i.BusinessValidation;

import com.ibm.garage.cpat.cp4i.FinancialMessage.FinancialMessage;

import io.reactivex.Flowable;
import io.smallrye.reactive.messaging.annotations.Broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Incoming;


@ApplicationScoped
public class BusinessValidation {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessValidation.class);

    // @Incoming annotation denotes the incoming channel that we'll be reading from.
    // The @Outgoing denotes the outgoing channel that we'll be sending to.
    @Incoming("pre-business-check")
    @Outgoing("post-business-check")
    @Broadcast
    public Flowable<FinancialMessage> processCompliance(FinancialMessage financialMessage) {

        FinancialMessage receivedMessage = financialMessage;

        LOGGER.info("Message received from topic = {}", receivedMessage);

        if (receivedMessage.business_validation && !receivedMessage.trade_enrichment &&
            !receivedMessage.schema_validation && !receivedMessage.technical_validation &&
            !receivedMessage.compliance_services) {
            /*
            Since Business Validation is the last service along the "conveyor belt" we just change
            the boolean to false to indicate that it's finished.
            */
            receivedMessage.business_validation = false;

            return Flowable.just(receivedMessage);
        }

        else {
            return Flowable.empty();
        }
    }
}