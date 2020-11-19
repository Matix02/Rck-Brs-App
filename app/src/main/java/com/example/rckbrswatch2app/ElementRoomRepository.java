package com.example.rckbrswatch2app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class ElementRoomRepository {

    private Application application;
    private ElementDao elementDao;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Element>> elementLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Element>> elementLiveFirebaseData = new MutableLiveData<>();

    private long rowIdOfTheItemInserted;

    //Moj szajs
    List<Element> elementList;
    private MutableLiveData<List<Element>> elementFilterLiveData = new MutableLiveData<>();

    public ElementRoomRepository(Application application) {
        this.application = application;

        ElementDatabase elementDatabase = ElementDatabase.getInstance(application);
        elementDao = elementDatabase.getElementDao();

        compositeDisposable.add(elementDao.getElements()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(elements -> elementLiveData.postValue(elements)));
    }

    public MutableLiveData<List<Element>> getElementLiveData() {
        return elementLiveData;
    }

    public void clear() { compositeDisposable.clear(); }

   /* public void createElement(int id, final String title, final String category, final String reccomendation, Boolean isWached) {
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
    }*/

    public MutableLiveData<List<Element>> equalData(List<Element> elements){
        compositeDisposable.add(Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                elementLiveFirebaseData.postValue(elements);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
                Toast.makeText(application.getApplicationContext(), "List has been updated successfully ", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
            }
        }));

        return elementLiveFirebaseData;
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

    public void updateElement(final long id, final String title) {
        compositeDisposable.add(Completable.fromAction(() -> elementDao.updateBlankElement(id, title))
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

    public void deleteAllElements() {
        compositeDisposable.add(Completable.fromAction(() -> elementDao.deleteAllElements())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "RoomDB has been cleared successfully", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    public MutableLiveData<List<Element>> filterList(final List<Element> elements, boolean state)
    {

        elementList = new ArrayList<>();
        compositeDisposable.add(Observable.fromArray(elements)
                .flatMap((Function<List<Element>, Observable<Element>>) elements1 -> Observable.fromArray(elements1.toArray(new Element[0])))
                .filter(element -> {
                    if(element.getCategory().equals("Film")&&state)
                        return true;
                    else return element.getCategory().equals("Gra") && !state;
                })
        .subscribeWith(new DisposableObserver<Element>() {
            @Override
            public void onNext(Element element) {
                Log.d("Bufor", "onNext " + element.getTitle());
                elementList.add(element);
            }
            @Override
            public void onError(Throwable e) {
                Log.d("Bufor", "Error");
            }

            @Override
            public void onComplete() {
                Log.d("Bufor", "onComplete " + elementList.size());
                elementFilterLiveData.postValue(elementList);
            }
        }));

        return elementFilterLiveData;
      /*  compositeDisposable.add(Completable.fromObservable(Observable.fromIterable(elements))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(application.getApplicationContext(), "Filter successfully!!!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(application.getApplicationContext(), "You fucked it up!!!", Toast.LENGTH_LONG).show();
                    }
                }));*/
    }
    public void filterElement(final Element element) {
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
}
