package xxxxx;

/**
 * Created by Aleksey on 14.07.2016.
 */

@InjectViewState
public class AccrualsPresenter extends MvpPresenter<AccrualsView> {

	private final static String TAG = AccrualsPresenter.class.getSimpleName();
	private Number number;
	private Subscription accrualsSubscription;

	public void init(Number pNumber) {
		number = pNumber;
		getViewState().initViews(number);
	}


	public void loadAccruals() {
		accrualsSubscription = Api.get.getAccruals(mNumber.getId())
								.map(this::filterNull)
				                .subscribe(response -> {
				                    if (response.isSuccess()) {
				                    	if (response.getData().size() > 0) {
				                        	getViewState().setItems(response.getData());
				                        } else {
				                        	getViewState().setPlaceholer();
				                        }
				                    } else {
				                        getViewState().onApiError(response.message);
				                    }
				                }, error-> {
				                	L.e(TAG, error.getMessage());
					                    getViewState().onApiError(Constants.NETWORK_BASE_ERROR);
				                });
	}

	public void stop() {
        if (accrualsSubscription != null && !accrualsSubscription.isUnsubscribed()) {
            accrualsSubscription.unsubscribe();
        }
	}

    private List<Accrual> filterNull(List<Accrual> accruals) {
        List<Accrual> filteredList = new LinkedList<>();
        for (Accrual accrual : accruals) {
            if (accrual.getSum() > 0 ) {
                filteredList.add(accrual);
            }
        }
        return filteredList;
    }
}
