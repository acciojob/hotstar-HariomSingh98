package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();

        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        subscription.setUser(user);
        user.setSubscription(subscription);

        int amt = 0;
        int numberOfScreens = subscriptionEntryDto.getNoOfScreensRequired();
        SubscriptionType type = subscriptionEntryDto.getSubscriptionType();

        if(type.equals(SubscriptionType.BASIC)){
            amt = 500 + 200*numberOfScreens;
        } else if (type.equals(SubscriptionType.PRO)) {
            amt = 800 + 250*numberOfScreens;
        }else{
            amt = 1000 + 350*numberOfScreens;
        }

        subscription.setTotalAmountPaid(amt);

        return amt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        int priceToPay  = 0;

        Subscription subscription = user.getSubscription();
        int screens = subscription.getNoOfScreensSubscribed();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO)){
          subscription.setSubscriptionType(SubscriptionType.ELITE);
          int old_amt = subscription.getTotalAmountPaid();
          int new_amt = 1000 + 350*screens;
          priceToPay = new_amt - old_amt;
          subscription.setTotalAmountPaid(new_amt);
        }
        else{
            subscription.setSubscriptionType(SubscriptionType.PRO);
            int old_amt = subscription.getTotalAmountPaid();
            int new_amt = 800 + 250*screens;
            priceToPay = new_amt - old_amt;
            subscription.setTotalAmountPaid(new_amt);
        }

        subscriptionRepository.save(subscription);

        return priceToPay;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int revenue=0;

        for(Subscription subscription : subscriptions){
            revenue += subscription.getTotalAmountPaid();
        }

        return revenue;
    }

}
