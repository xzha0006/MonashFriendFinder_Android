package com.johnsyard.monashfriendfinder.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MultiSelectionSpinner extends AppCompatSpinner implements OnMultiChoiceClickListener {

  public interface OnMultipleItemsSelectedListener{
    void getSelectedStrings(List<String> strings);
  }
  private OnMultipleItemsSelectedListener listener;

  String[] initItems = null;
  boolean[] mSelection = null;
  boolean[] mSelectionDefault = null;
  String initItemsDefault = null;

  ArrayAdapter<String> simpleAdapter;

  public MultiSelectionSpinner(Context context) {
    super(context);

    simpleAdapter = new ArrayAdapter<>(context,
            android.R.layout.simple_spinner_item);
    super.setAdapter(simpleAdapter);
  }

  public MultiSelectionSpinner(Context context, AttributeSet attrs) {
    super(context, attrs);

    simpleAdapter = new ArrayAdapter<>(context,
            android.R.layout.simple_spinner_item);
    super.setAdapter(simpleAdapter);
  }

  public void setListener(OnMultipleItemsSelectedListener listener){
    this.listener = listener;
  }

  public void onClick(DialogInterface dialog, int which, boolean isChecked) {
    if (mSelection != null && which < mSelection.length) {
      mSelection[which] = isChecked;
      simpleAdapter.clear();
      simpleAdapter.add(buildSelectedItemString());
    } else {
      throw new IllegalArgumentException(
              "Argument 'which' is out of bounds.");
    }
  }

  @Override
  //while click the spinner
  public boolean performClick() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Please select the keywords:");
    builder.setMultiChoiceItems(initItems, mSelection, this);
    initItemsDefault = buildSelectedItemString();
    //button submit
    builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        System.arraycopy(mSelection, 0, mSelectionDefault, 0, mSelection.length);
        listener.getSelectedStrings(getSelectedStrings());
      }
    });
    //button cancel
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        simpleAdapter.clear();
        simpleAdapter.add(initItemsDefault);
        System.arraycopy(mSelectionDefault, 0, mSelection, 0, mSelectionDefault.length);
      }
    });
    builder.show();
    return true;
  }
  //set items
  public void setItems(String[] items) {
    initItems = items;
    mSelection = new boolean[initItems.length];
    mSelectionDefault = new boolean[initItems.length];
    simpleAdapter.clear();
    simpleAdapter.add(initItems[0]);
    Arrays.fill(mSelection, false);
    mSelection[0] = true;
    mSelectionDefault[0] = true;
  }

  //set default selection
  public void setSelection(int[] selectedIndices) {
    for (int i = 0; i < mSelection.length; i++) {
      mSelection[i] = false;
      mSelectionDefault[i] = false;
    }
    for (int index : selectedIndices) {
      if (index >= 0 && index < mSelection.length) {
        mSelection[index] = true;
        mSelectionDefault[index] = true;
      } else {
        throw new IllegalArgumentException("Index " + index
                + " is out of bounds.");
      }
    }
    simpleAdapter.clear();
    simpleAdapter.add(buildSelectedItemString());
  }

  //get selected strings
  public List<String> getSelectedStrings() {
    List<String> selection = new LinkedList<>();
    for (int i = 0; i < initItems.length; ++i) {
      if (mSelection[i]) {
        selection.add(initItems[i]);
      }
    }
    return selection;
  }

  //build the spinner selected string
  private String buildSelectedItemString() {
    StringBuilder sb = new StringBuilder();
    boolean foundOne = false;

    for (int i = 0; i < initItems.length; ++i) {
      if (mSelection[i]) {
        if (foundOne) {
          sb.append(", ");
        }
        foundOne = true;

        sb.append(initItems[i]);
      }
    }
    return sb.toString();
  }
}