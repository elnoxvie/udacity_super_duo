package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    public static final String ISBN_PREFIX = "978";
    int ISBN_LENGTH = 10;

    private EditText ean;
   private final int LOADER_ID = 1;
   private View rootView;
   private final        String EAN_CONTENT   = "eanContent";
   private static final String SCAN_FORMAT   = "scanFormat";
   private static final String SCAN_CONTENTS = "scanContents";

   private String mScanFormat   = "Format:";
   private String mScanContents = "Contents:";
   private ProgressBar mProgressBar;
   private View        mContainer;


   public AddBook() {
   }

   @Override
   public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
      if (ean != null) {
         outState.putString(EAN_CONTENT, ean.getText().toString());
      }
   }

   @Override
   public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
      ean = (EditText) rootView.findViewById(R.id.ean);
      mContainer = rootView.findViewById(R.id.container);
      mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

      rootView.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            // This is the callback method that the system will invoke when your button is
            // clicked. You might do this by launching another app or by including the
            //functionality directly in this app.
            // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
            // are using an external app.
            //when you're done, remove the toast below.
//                Context context = getActivity();
//                CharSequence text = "This button should let you scan a book for its barcode!";
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, text, duration);
//                toast.show();

            IntentIntegrator.forSupportFragment(AddBook.this).initiateScan();
         }
      });

      rootView.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            ean.setText("");
            clearFields();
         }
      });

      rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean.getText().toString());
            bookIntent.setAction(BookService.DELETE_BOOK);
            getActivity().startService(bookIntent);
            ean.setText("");
         }

      });

      ean.addTextChangedListener(mIsbnTextWatcher);

      if (savedInstanceState != null) {
         ean.setText(savedInstanceState.getString(EAN_CONTENT));
         ean.setHint("");
      }

      return rootView;
   }

    @Override
    public void onStart() {
        super.onStart();
       // getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
   public void onDestroyView() {
      ean.removeTextChangedListener(mIsbnTextWatcher);
      super.onDestroyView();
   }

   private TextWatcher mIsbnTextWatcher = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
         String ean = s.toString();

         //we dont want to unnecessarily run the service. For now, we will program it to not do anythin if less than 10 characters
         if (ean.length() < 10) {
              return;
         }
         //catch isbn10 numbers
         if (ean.length() == 10 && !ean.startsWith(ISBN_PREFIX)) {
            ean = ISBN_PREFIX + ean;
         }

          if (!Utility.isNetworkConnected(getActivity())){
              Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
              return;
          }


          /***
           * As per commented, this seems to be not conforming with the use case, thus this is disable
           * for now.
           */
//         if (ean.length() < 13) {
//            clearFields();
//            return;
//         }

         //Once we have an ISBN, start a book intent
         Intent bookIntent = new Intent(getActivity(), BookService.class);
         bookIntent.putExtra(BookService.EAN, ean);
         bookIntent.setAction(BookService.FETCH_BOOK);
         getActivity().startService(bookIntent);
         AddBook.this.restartLoader();
      }
   };


   public void showProgress(final boolean progress) {
      getActivity().runOnUiThread(new Runnable() {
         @Override
         public void run() {
            if (progress) {
               mProgressBar.setVisibility(View.VISIBLE);
               mContainer.setVisibility(View.INVISIBLE);
            } else {
               mProgressBar.setVisibility(View.INVISIBLE);
               mContainer.setVisibility(View.VISIBLE);
            }

         }
      });
   }


   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

      if (result != null) {
         if (result.getContents() == null) {

         } else {
            ean.setText(result.getContents().toString());
            restartLoader();
         }
         // At this point we may or may not have a reference to the activity
      }

      super.onActivityResult(requestCode, resultCode, data);
   }

   private void restartLoader() {

      Loader loader = getLoaderManager().getLoader(LOADER_ID);
       if (loader != null && !loader.isReset()) {
           getLoaderManager().restartLoader(LOADER_ID, null, this);
       } else {
           getLoaderManager().initLoader(LOADER_ID, null, this);
       }

      //getLoaderManager().restartLoader(LOADER_ID, null, this);
   }

   @Override
   public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

       //We don't want to return null. This seems to stop the Content Observer from doing it's job
//      if (ean.getText().length() == 0) {
//         return null;
//      }

      String eanStr = ean.getText().toString();
       if (eanStr.length() == ISBN_LENGTH  && !eanStr.startsWith(ISBN_PREFIX)) {
         eanStr = ISBN_PREFIX + eanStr;
      }

      showProgress(true);
      return new CursorLoader(
              getActivity(),
              AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
              null,
              null,
              null,
              null
      );
   }

   @Override
   public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
      showProgress(false);
      if (!data.moveToFirst()) {
         return;
      }


      String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
      ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

      String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
      ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

      String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
      if (authors != null) {
         String[] authorsArr = authors.split(",");
         ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
         ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
      }
      String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
      if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
         Glide.with(this).load(imgUrl).crossFade().fitCenter().into((ImageView) rootView.findViewById(R.id.bookCover));
         rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
      }

      String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
      ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

      rootView.findViewById(R.id.clear_button).setVisibility(View.VISIBLE);
      rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
   }

   @Override
   public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

   }

   private void clearFields() {
      ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
      ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
      ((TextView) rootView.findViewById(R.id.authors)).setText("");
      ((TextView) rootView.findViewById(R.id.categories)).setText("");
      rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
      rootView.findViewById(R.id.clear_button).setVisibility(View.INVISIBLE);
      rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
   }

   @Override
   public void onAttach(Activity activity) {
      super.onAttach(activity);
      activity.setTitle(R.string.scan);
   }
}
