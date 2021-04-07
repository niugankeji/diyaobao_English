#include <jni.h>
#include <stdio.h>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include "munsell.h"

using namespace std;
using namespace cv;

extern "C" {
JNIEXPORT void JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_downProcess(JNIEnv* env, jobject obj, jlong addrRgba, jintArray numParameter);

JNIEXPORT void JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_upProcess(JNIEnv* env, jobject obj, jlong addrRgba, jintArray numParameter);

JNIEXPORT jstring JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_Process(JNIEnv* env, jobject obj, jlong addrRgba,jint FeatureMode, jintArray numParameter);

//�������㴦����
JNIEXPORT void JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_downProcess(JNIEnv* env, jobject obj, jlong addrRgba, jintArray numParameter)
{
    Mat& img = *(Mat*)addrRgba;
    jint* Parameter = env->GetIntArrayElements(numParameter, 0);
    int Threshold = 0;


    Mat result;
    if(result.empty())
	{
		result.create(img.size(), CV_8UC1);
	}
    for(int i=0; i<img.rows; i++)
	{
		uchar *ptri = img.ptr<uchar>(i);
		uchar *ptrr = result.ptr<uchar>(i);
		for(int j=0; j<img.cols; j++)
		{
			int temp = 2*(int)ptri[3*j+1] - (int)ptri[3*j] - (int)ptri[3*j+2];
			if(temp <= Threshold)
			{
				ptrr[j] = 0;
			}else
			{
				ptrr[j] = 255;
			}
		}
	}

    erode(result, result, Mat());
    dilate(result, result, Mat());


    //��ʼͳ�Ʊ�������
    for(int i=0; i<result.rows; i++)
	{
		uchar *ptrr = result.ptr<uchar>(i);
		for(int j=0; j<result.cols; j++)
		{
			if(ptrr[j] == 255)
				Parameter[0]++;
		}
	}


    cvtColor(result, img, CV_GRAY2BGR );

}


//�������㴦����
JNIEXPORT void JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_upProcess(JNIEnv* env, jobject obj, jlong addrRgba, jintArray numParameter)
{
	Mat& img = *(Mat*)addrRgba;
	jint* Parameter = env->GetIntArrayElements(numParameter, 0);
	int Threshold = 0;
	unsigned char minRed = 100;

	Mat result;
	if(result.empty())
	{
		result.create(img.size(), CV_8UC1);
	}
	for(int i=0; i<img.rows; i++)
	{
		uchar *ptri = img.ptr<uchar>(i);
		uchar *ptrr = result.ptr<uchar>(i);
		for(int j=0; j<img.cols; j++)
		{
			int temp = (int)ptri[3*j] - (int)ptri[3*j+2];
			if((temp > Threshold && ptri[3*j+2] > minRed)||ptri[3*j]==255)
			{
				ptrr[j] = 0;
			}else
			{
				ptrr[j] = 255;
			}
		}
	}

	erode(result, result, Mat());
	dilate(result, result, Mat());


	//��ʼͳ�Ʊ�������
	for(int i=0; i<result.rows; i++)
	{
		uchar *ptrr = result.ptr<uchar>(i);
		for(int j=0; j<result.cols; j++)
		{
			if(ptrr[j] == 255)
				Parameter[0]++;
		}
	}


	cvtColor(result, img, CV_GRAY2BGR );

}


JNIEXPORT jstring JNICALL Java_com_leman_diyaobao_fragment_CameraFragment_Process(JNIEnv* env, jobject obj, jlong addrRgba,jint FeatureMode, jintArray numParameter)
{
	Mat& img = *(Mat*)addrRgba;
	jboolean isCopy = false;
	jint* Parameter = env->GetIntArrayElements(numParameter, &isCopy);
	Mat result(img.size(), CV_8UC1);
	Mat imgHSV;
	cvtColor(img, imgHSV, CV_RGB2HSV);

	if(FeatureMode == 1)	//���Ͷ�Hͨ��
	{
		for(int i=0; i<result.rows; i++)
		{
			uchar *PtrHSV = imgHSV.ptr<uchar>(i);
			uchar *Ptrresult = result.ptr<uchar>(i);
			for(int j=0; j<result.cols; j++)
			{
				Ptrresult[j] = PtrHSV[3*j];
			}
		}
		threshold(result, result, 0, 255, THRESH_OTSU);
	}else if(FeatureMode == 2)	//��ɫ����
	{
		for(int i=0; i<result.rows; i++)
		{
			uchar *Ptrimg = img.ptr<uchar>(i);
			uchar *Ptrresult = result.ptr<uchar>(i);
			for(int j=0; j<result.cols; j++)
			{
				Ptrresult[j] = Ptrimg[3*j+2];
			}
		}
		threshold(result, result, 0, 255, THRESH_BINARY_INV|THRESH_OTSU);
	}else if(FeatureMode == 3)	//Vͨ��
	{
		for(int i=0; i<result.rows; i++)
		{
			uchar *PtrHSV = imgHSV.ptr<uchar>(i);
			uchar *Ptrresult = result.ptr<uchar>(i);
			for(int j=0; j<result.cols; j++)
			{
				Ptrresult[j] = PtrHSV[3*j+2];
			}
		}
		threshold(result, result, 0, 255, THRESH_OTSU);
	}else if(FeatureMode == 4)	//�̶�ָ��
	{
		Mat mResult(img.size(), CV_16SC1);
		short minValue = 10000;
		short maxValue = -10000;
		for(int i=0; i<mResult.rows; i++)
		{
			uchar *Ptrimg = img.ptr<uchar>(i);
			short *PtrmResult = mResult.ptr<short>(i);
			for(int j=0; j<mResult.cols; j++)
			{
				PtrmResult[j] = 2*(short)Ptrimg[3*j+1]-(short)Ptrimg[3*j]-(short)Ptrimg[3*j+2];
				if(minValue > PtrmResult[j])
				{
					minValue = PtrmResult[j];
				}
				if(maxValue < PtrmResult[j])
				{
					maxValue = PtrmResult[j];
				}
			}
		}
		short Value = maxValue - minValue;

		//��һ����0~255
		for(int i=0; i<mResult.rows; i++)
		{
			short *PtrmResult = mResult.ptr<short>(i);
			uchar *Ptrresult = result.ptr<uchar>(i);
			for(int j=0; j<mResult.cols; j++)
			{
				Ptrresult[j] = (uchar)((PtrmResult[j]-minValue)*255.0f/Value);
			}
		}
		threshold(result, result, 0, 255, THRESH_BINARY|THRESH_OTSU);
	}else
	{

	}


	//��ʼͳ�Ʊ�������
	for(int i=0; i<result.rows; i++)
	{
		uchar *ptrr = result.ptr<uchar>(i);
		for(int j=0; j<result.cols; j++)
		{
			if(ptrr[j] == 255)
				Parameter[0]++;
		}
	}

	//������ɫ��ֵ
	double colorTmp[3] = {0.0};
	int i, j, count=0;
    for (i=0; i<3; i++) {
        Parameter[i+1] = jint(0);
    }
	for(i=0; i<img.rows; i+=5)
    {
        uchar *Ptrimg = img.ptr<uchar>(i);
        for(j=0; j<img.cols; j+=3)
        {
             colorTmp[0] += double(Ptrimg[j])/255;
             colorTmp[1] += double(Ptrimg[j+1])/255;
             colorTmp[2] += double(Ptrimg[j+2])/255;
             count++;
        }
    }
    double RGB[3] = {0.0};

    for (i=0; i<3; i++) {
        Parameter[i+1] = jint(255.0 * colorTmp[i]/count);
        RGB[i] = colorTmp[i]/count;
    }

    jstring munsell_color;
    munsell_color = env->NewStringUTF(munsell_name_table[munsell(RGB)].c_str());
//	cvtColor(result, img, CV_GRAY2BGR );

	env->ReleaseIntArrayElements(numParameter, Parameter, 0);
	return munsell_color;
}

}

