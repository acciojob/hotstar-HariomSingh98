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
        String seriesName = webSeriesEntryDto.getSeriesName();

        if(webSeriesRepository.findBySeriesName(seriesName) != null)
            throw new Exception("Series is already present");

        WebSeries series = new WebSeries();
        series.setSeriesName(webSeriesEntryDto.getSeriesName());
        series.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        series.setRating(webSeriesEntryDto.getRating());
        double rating = webSeriesEntryDto.getRating();
        series.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        productionHouse.getWebSeriesList().add(series);

        int totalSeries = productionHouse.getWebSeriesList().size();
        double prevRating = productionHouse.getRatings();

        double updatedRating = prevRating + (rating-prevRating)/totalSeries;


        productionHouse.setRatings(updatedRating);


        series.setProductionHouse(productionHouse);


       ProductionHouse saved =  productionHouseRepository.save(productionHouse);


        return saved.getWebSeriesList().get(saved.getWebSeriesList().size()-1).getId();


    }

}
