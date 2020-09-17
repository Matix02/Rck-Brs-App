package com.example.rckbrswatch2app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ElementRoomRepository {

    private Application application;
    private ElementDao elementDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();
    private long rowIdOfTheItemInserted;

    //Mój szajs
    List<Element> elementList = new ArrayList<>();

    public ElementRoomRepository(Application application) {
        this.application = application;

        ElementDatabase elementDatabase = ElementDatabase.getInstance(application);
        elementDao = elementDatabase.getElementDao();

        compositeDisposable.add(elementDao.getElements()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .flatMap((Function<List<Element>, Observable<Element>>) elements -> Observable.fromArray(elements.toArray(new Element[0])))
                .filter(new Predicate<Element>() {
                    @Override
                    public boolean test(Element element) throws Exception {
                        return element.getCategory().equals("Gra");
                    }
                })
                .reduce(new ArrayList<Element>(), (elements, element) -> {
                    Log.d("Bufor", "Reduce - Constructor");
                    elements.add(element);

                    elementLiveData.postValue(elements);
                    return elements;
                })
                .subscribe(elements -> {
                    Log.d("Bufor", "Subscribe - Constructor");
                    elementLiveData.postValue(elements);
                })
        );
    }

    public MutableLiveData<List<Element>> getElementLiveData() {
        return elementLiveData;
    }

    public void createElement(int id, final String title, final String category, final String reccomendation, Boolean isWached) {
        compositeDisposable.add(Completable.fromAction(() -> rowIdOfTheItemInserted = elementDao.addElement(new Element(id, title, category, isWached, reccomendation)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Element has been added successfully " + rowIdOfTheItemInserted, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    public void createElement(Element element) {
        compositeDisposable.add(Completable.fromAction(() -> elementDao.addElement(element))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Element has been added successfully ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Error", "Error is = " + e);
                        Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    }
                }));
    }

    public void updateElement(final Element element) {
        compositeDisposable.add(Completable.fromAction(() -> elementDao.updateElement(element))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Element has been updated successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    public void deleteElement(final Element element) {
        compositeDisposable.add(Completable.fromAction(() -> elementDao.deleteElement(element))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Element has been deleted successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    public void clear() {
        compositeDisposable.clear();
    }

}
