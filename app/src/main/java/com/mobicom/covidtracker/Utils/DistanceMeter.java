package com.mobicom.covidtracker.Utils;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.mobicom.covidtracker.Models.ModelInput;

import java.util.function.Consumer;

public class DistanceMeter {
    private FirebaseCustomLocalModel localModel;

    public void initDistanceModel(){
        localModel = new FirebaseCustomLocalModel.Builder()
                .setAssetFilePath("model.tflite")
                .build();
    }





    public void predictDistance(ModelInput mInput, Consumer<Double> callback){
        FirebaseModelInterpreter interpreter;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            try{
                FirebaseModelInputOutputOptions inputOutputOptions =
                        new FirebaseModelInputOutputOptions.Builder()
                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,8})
                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,1})
                                .build();
                float[][] input = new float[1][8];
                input[0][0]=mInput.getSenderBatteryCap();
                input[0][1]=mInput.getReceiverBatteryLevel();
                input[0][2]=mInput.getReceiverBatteryLevel();
                input[0][3]=mInput.getReceiverBTVersion();
                input[0][4]=mInput.getSenderTemp();
                input[0][5]=mInput.getReceiverTemp();//
                input[0][6]=mInput.getReceiverBTVersion();
                input[0][7]=-mInput.getRssi();
                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your model requires
                        .build();
                interpreter.run(inputs, inputOutputOptions)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseModelOutputs>() {
                                    @Override
                                    public void onSuccess(FirebaseModelOutputs result) {
                                        float[][] output = result.getOutput(0);
                                        float probability = output[0][0];
                                        if(probability>50f){
                                            Log.d("Bluel","close");
                                        }else {
                                            Log.d("Bluel","long");
                                        }


                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

            }catch (FirebaseMLException er){
            }
        } catch (FirebaseMLException e) {

        }
    }

}
