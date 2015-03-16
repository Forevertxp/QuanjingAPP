package com.quanjing.weitu.app.ui.common;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.quanjing.weitu.R;

public class MWTDualFragment extends Fragment {
    private Button _buttonA;
    private Button _buttonB;

    private String _titleA;
    private String _titleB;
    private Fragment _fragmentA;
    private Fragment _fragmentB;
    private FrameLayout _fragmentContainer;

    private Button _currentActiveButton;

    private int _buttonBackgroundDrawable;

    private MWTDualFragment() {

    }

    public MWTDualFragment(String titleA, Fragment fragmentA, String titleB, Fragment fragmentB) {
        _titleA = titleA;
        _titleB = titleB;
        _fragmentA = fragmentA;
        _fragmentB = fragmentB;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dual, container, false);

        _buttonA = (Button) view.findViewById(R.id.ButtonA);
        _buttonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragmentA();
            }
        });

        _buttonB = (Button) view.findViewById(R.id.ButtonB);
        _buttonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFragmentB();
            }
        });
        _fragmentContainer = (FrameLayout) view.findViewById(R.id.FragmentContainer);

        _buttonA.setBackgroundResource(_buttonBackgroundDrawable);
        _buttonB.setBackgroundResource(_buttonBackgroundDrawable);

        _buttonA.setText(_titleA);
        _buttonB.setText(_titleB);

        switchToFragmentA();

        return view;
    }

    private void switchToFragmentA() {
        if (_currentActiveButton == _buttonA) {
            return;
        }

        _currentActiveButton = _buttonA;

        _buttonA.setSelected(true);
        _buttonB.setSelected(false);
        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, _fragmentA).commit();
    }

    private void switchToFragmentB() {
        if (_currentActiveButton == _buttonB) {
            return;
        }

        _currentActiveButton = _buttonB;

        _buttonA.setSelected(false);
        _buttonB.setSelected(true);

        getFragmentManager().beginTransaction().replace(R.id.FragmentContainer, _fragmentB).commit();
    }

    public void setButtonBackgroundDrawable(int buttonBackgroundDrawable) {
        _buttonBackgroundDrawable = buttonBackgroundDrawable;
    }
}
