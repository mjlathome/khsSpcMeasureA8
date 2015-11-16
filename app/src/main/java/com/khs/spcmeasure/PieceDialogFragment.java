package com.khs.spcmeasure;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.khs.spcmeasure.entity.Piece;
import com.khs.spcmeasure.entity.Product;
import com.khs.spcmeasure.library.AlertUtils;
import com.khs.spcmeasure.library.CollectStatus;
import com.khs.spcmeasure.library.DateTimeUtils;
import com.khs.spcmeasure.library.ProgressUtils;
import com.khs.spcmeasure.library.SecurityUtils;

import java.util.Date;


// captures new piece details and launches measurement activity
public class PieceDialogFragment extends DialogFragment implements OnClickListener{

	private Long mProdId;
	private Date mCollectDate;
	private String mCollectDtStr;
	private OnNewPieceListener mListener;
	
	Button btnOkay, btnCancel;
	EditText edtLot, edtOperator;

	// container activity must implement this interface
	public interface OnNewPieceListener {
		public void onNewPieceCreated(Long pieceId);
	}
			
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// ensure host Activity implements the OnNewPieceListener interface
		try {
			mListener = (OnNewPieceListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + getString(R.string.text_must_implement_onnewpiecelistener));
		}		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);

		// ensure user is logged in
		if (!SecurityUtils.getIsLoggedIn(getActivity())) {
			AlertUtils.errorDialogShow(getActivity(), getString(R.string.sec_not_logged_in));
			dismiss();
		}

		// ensure user has access rights
		if (!SecurityUtils.getCanMeasure(getActivity())) {
			AlertUtils.errorDialogShow(getActivity(), getString(R.string.sec_cannot_measure));
			dismiss();
		}

		// unpack arguments	
		Bundle args = getArguments();
		if (args.containsKey(DBAdapter.KEY_PROD_ID)) {
			mProdId = args.getLong(DBAdapter.KEY_PROD_ID);
		}
		
		// verify arguments and exit upon error
		if (mProdId == null) {
			AlertUtils.errorDialogShow(getActivity(), getString(R.string.text_mess_prod_id_invalid));
			dismiss();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// inflate custom view
		View v = inflater.inflate(R.layout.piece_dialog_fragment, container, false);
				
		// extract widgets
		edtLot = (EditText) v.findViewById(R.id.edtLot);
		edtOperator = (EditText) v.findViewById(R.id.edtOperator);
		btnOkay = (Button) v.findViewById(R.id.btnPieceOk);
		btnCancel = (Button) v.findViewById(R.id.btnPieceCancel);		
		
		// TODO is this required to make it look more like a dialog?
		Dialog myDialog = getDialog();
		myDialog.setTitle(getString(R.string.title_new_piece));
		
		// extract Product
		// TODO need to handle situation when Prod Id is null.
		DBAdapter db = new DBAdapter(getActivity());
        db.open();
		Cursor c = db.getProduct(mProdId);
		Product p = db.cursorToProduct(c);
        c.close();
		db.close();
		
		// populate dialog widget values
		TextView txtProdName = (TextView) v.findViewById(R.id.txtProdName);
		txtProdName.setText(p.getName());
		
		mCollectDate = new Date();
		mCollectDtStr = DateTimeUtils.getDateTimeStr(mCollectDate);
		TextView txtCollectDt = (TextView) v.findViewById(R.id.txtCollectDt);
		txtCollectDt.setText(mCollectDtStr);

		// populate Operator
		edtOperator.setText(SecurityUtils.getUsername(getActivity()));

		// set button listeners
		btnOkay.setOnClickListener(this);
		btnCancel.setOnClickListener(this);		
		
		return v;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPieceOk:
			// validate fields
			if (validateFields() == false) {
				return;
			}
			
			// create progress dialog
			ProgressDialog progDiag = ProgressUtils.progressDialogCreate(getActivity(), getString(R.string.text_creating_piece));
			
			// TODO use try catch
			try {
				// show progress dialog
				progDiag.show();
				
				// open the DB
				DBAdapter db = new DBAdapter(getActivity());
				db.open();
				
				// create Piece
				long pieceNum = 1;
				Piece piece =  new Piece(mProdId, pieceNum, mCollectDate, edtOperator.getText().toString(), edtLot.getText().toString(), CollectStatus.OPEN);
				
				// insert Piece into the DB
				long pieceId = db.createPiece(piece);
								
				// close the DB			
				db.close();			
							
				dismiss();
				
				if (pieceId < 0) {
					AlertUtils.errorDialogShow(getActivity(), getString(R.string.text_piece_create_failed));
				} else {
					// inform the Activity of the new Setup
					mListener.onNewPieceCreated(pieceId);
				}				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// close progress dialog
				progDiag.dismiss();
			}
				
			// Toast.makeText(getActivity(), "OKAY", Toast.LENGTH_LONG).show();
			break;
		case R.id.btnPieceCancel:
			dismiss();
			Toast.makeText(getActivity(), getString(R.string.text_piece_create_cancelled), Toast.LENGTH_LONG).show();
			break;			
		}
	}

	// returns true if all on-screen fields are valid, otherwise false
	private boolean validateFields() {
		
		// validate operator
		if (edtOperator.getText().length() == 0) {
			AlertUtils.errorDialogShow(getActivity(), getString(R.string.text_not_set_operator));
			edtOperator.requestFocus();
			return false;
		}
					
		// validate lot
		if (edtLot.getText().length() != 6) {
			AlertUtils.errorDialogShow(getActivity(), getString(R.string.text_not_set_lot));
			edtLot.requestFocus();
			return false;
		}			
				
		return true;
	}
	
}
