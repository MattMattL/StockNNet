# StockNNet

![Running the application](readme/example.PNG)

## Overview
A mini project where deep neural networks and yahoofinance-api are used to generate short term predictions (5-10 days) of the day-to-day stock data.

The green lines are the historic data and the white lines are the predictions made by the DNN, the four quadrants display opening, closing, min and max price (y-axis) against time (x-axis).

## Technical details
* The input vector contains opening, closing, min and max price of multiple days.
* The calibration algorithm automatically simulates and picks the network depth and input size based on overall error.
* Once trained the algorithm shows 5-10 days of predicted future data for opening, closing, min and max price.

## Why it is NOT reliable
* The design lacks well designed pre-processing layers.
* A rapid overfitting occurs during the training due to the simplicity of the neural architecture (can be seen in the graphs).
* Supplied training data does not include real-life data, e.g., overall economy or news.
