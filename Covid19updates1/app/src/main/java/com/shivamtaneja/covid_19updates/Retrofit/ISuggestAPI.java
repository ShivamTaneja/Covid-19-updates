package com.shivamtaneja.covid_19updates.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISuggestAPI  {
    @GET("complete/search")
    Observable<String> getSuggestFromYouTube(@Query("q") String query,
                                             @Query("client") String client,
                                             @Query("hi") String language,
                                             @Query("ds") String restrict);

    @GET("complete/search")
    Observable<String> getSuggestFromGoogle( @Query("q") String query,
                                            @Query("client") String client,
                                            @Query("hi") String language);
}
