package xxxx;

/**
 * Created by Aleksey on 09.07.2016.
 */
public enum Api {

    get;

    private final String TAG = Api.class.getSimpleName();
    private final ApiInterface service;
    private OkHttpClient mClient;
    private Retrofit mRetrofit;


    Api() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        mClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(
                        chain -> {
                            Request original = chain.request();

                            String token = Storage.getToken();
        
                            Request.Builder requestBuilder = original.newBuilder()
                                    .header("Client-Os", "android")
                                    .header("Client-Os-Version", Config.getOSVersion())
                                    .header("Client-Version", BuildConfig.VERSION_NAME)
                                    .method(original.method(), original.body());

                            if (token != null && !token.isEmpty()) {
                                requestBuilder.addHeader("Access-Token", token);
                            }

                            Request request = requestBuilder.build();
                            Response response = chain.proceed(request)

                            return response;
                        }
                ).build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_HOST)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(mClient)
                .build();

        service = mRetrofit.create(ApiInterface.class);
    }


    public Observable<CommonResponse<Void>> checkApi() {
        return service.apiCheck()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(this::parseErrorBody)
                .unsubscribeOn(Schedulers.io());
    }

    public Observable<CommonResponse<Void>> logout() {
        return service.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(this::parseErrorBody)
                .unsubscribeOn(Schedulers.io());
    }

    //<......SOME TEXT CUTTED.....>/////////////


    public Observable<CommonResponse<Number>> getNumberInfo(int pNumberId) {
        return service.numberInfo(pNumberId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(this::parseErrorBody)
                .unsubscribeOn(Schedulers.io());
    }


    public Observable<CommonResponse<List<Accrual>>> getAccruals(int number) {
        return service.getAccruals(number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(this::parseErrorBody)
                .unsubscribeOn(Schedulers.io());
    }

    private CommonResponse parseErrorBody(Throwable throwable) {
        if (throwable instanceof HttpException) {
            ResponseBody body = ((HttpException) throwable).response().errorBody();
            Gson gson = new Gson();
            try {
                CommonResponse<GeneralError> response = gson.fromJson(body.string(), CommonResponse.class);
                return response;
            } catch (IOException e) {
                L.e(TAG, "Parse failed\n" + e.getMessage);
            }
            return CommonResponse.getErrorResponse(throwable.getMessage());
        } else {
            return CommonResponse.getErrorResponse(throwable.getMessage());
        }
    }


}
