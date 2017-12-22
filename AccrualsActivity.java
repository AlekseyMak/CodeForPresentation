package xxxx;

/**
 * Created by Aleksey on 14.07.2016.
 */
public class AccrualsActivity extends BaseMVPActivity implements AccrualsView {

    private AccrualHistoryAdapter mAdapter;

    private  static final String EXTRA_NUMBER = "extra_number";
    private static final String TAG = AccrualsActivity.class.getSimpleName();


    public static Intent getIntent(Context context, @NotNull Number number) {
        Intent intent = new Intent(context, AccrualsActivity.class);
        intent.putExtra(EXTRA_NUMBER, number);
        return intent;
    }

    @InjectPresenter
    GreetingPresenter presenter;

    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.title_toolbar)
    TextView toolbarTitle;
    @Bind(R.id.empty_list_placeholder)
    View placeholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accruals);
        ButterKnife.bind(this);
        NUmber number = (Number) getIntent().getExtras().getSerializable(EXTRA_NUMBER);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showProgress();

        if (number != null) {
            presenter.init(mNumber);
        } else {
            throw new IllegalStateException("Supplied number is null, check getIntent method");
        }
        mAdapter = new AccrualHistoryAdapter(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initViews(Number number) {

        toolbarTitle.setText(String.format(getString(R.string.accruals_title), number.getFormattedPhone()));
    }

    @Override
    public void onApiError(String msg) {
        hideProgress();
        showErrorToast(msg);
    }

    @Override
    public void setPlaceholder() {
        placeholder.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        hideProgress();
    }

    @Override
    public void setItems(List<Accrual> pList) {
        placeholder.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        mAdapter.setItems(pList);
        hideProgress();
    }
}
