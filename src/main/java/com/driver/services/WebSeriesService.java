package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo
        WebSeries series  =  null;
        series =  webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());

        if(series!=null)throw  new Exception("Series is already Present");

        series.setSeriesName(webSeriesEntryDto.getSeriesName());
        series.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        series.setRating(webSeriesEntryDto.getRating());
        double rating = webSeriesEntryDto.getRating();
        series.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();


        int totalSeries = productionHouse.getWebSeriesList().size();
        double prevRating = productionHouse.getRatings();

        double newRating = (prevRating + rating)/(totalSeries+1);

        productionHouse.setRatings(newRating);
        productionHouse.getWebSeriesList().add(series);

        series.setProductionHouse(productionHouse);


       ProductionHouse saved =  productionHouseRepository.save(productionHouse);


        return saved.getWebSeriesList().get(saved.getWebSeriesList().size()-1).getId();


    }

}
