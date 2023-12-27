# StockNNet

![Running the application](readme/example.PNG)

## Overview
A mini project where deep neural networks and yahoofinance-api are used to generate short term predictions (5-10 days) of the day-to-day stock data.

## "Technical" Details
* Input vector consists of opening, closing, min and max price of multiple days.
* The calibration algorithm automatically picks the network depth and the input size that minimise overall error.
* Once trained, the algorithm shows 5-10 days of predicted future data for opening, closing, max and min values.

## Why It Doesn't Work Well
* It was just a quick test for fun.
* Lack of well designed pre-processing layers.
* Rapid overfitting during the training due to the simplicity of the neural architecture.
* Not enough input data (e.g., real-life news somehow processed into numerics, etc.?)
