package com.example.camaleon.bionicsaa.orientation;

/**
 * Created by camaleon on 3/02/15.
 */
public class orientationManager {
    public orientationManager(int ejeHombro,int ejeCodo,int ejeMuneca,int ejePinza){

        totalPos = new int[]{ejeHombro,ejeCodo,ejeMuneca,ejePinza};

    }

    //Retorna la posicion total
    public int[] rotaHombro(int rotacion){
        totalPos[POS_HOMBRO]+=rotacion;
        return totalPos;
    }
    public int[] rotaCodo(int rotacion){
        totalPos[POS_CODO]+=rotacion;
        return totalPos;
    }
    public int[] rotaMuneca(int rotacion){
        totalPos[POS_MUNECA]+=rotacion;
        return totalPos;
    }
    public int[] rotaPinza(int rotacion){
        totalPos[POS_PINZA]+=rotacion;
        return totalPos;
    }


    public int[] getPos(){return totalPos;}
    public int getPosHombro(){return totalPos[POS_HOMBRO]; }
    public int getPosCodo(){return totalPos[POS_CODO]; }
    public int getPosMuneca(){return totalPos[POS_MUNECA]; }
    public int getPosPinza(){return totalPos[POS_PINZA]; }

    private int[] totalPos;

    //Posiciones de las partes dentro del arreglo totalPos
    private static int POS_HOMBRO = 0;
    private static int POS_CODO = 1;
    private static int POS_MUNECA = 2;
    private static int POS_PINZA = 3;



}

