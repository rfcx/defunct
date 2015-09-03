import numpy as np
from signal_likelihood import SignalLikelihood
import unittest
from numpy.testing import assert_array_almost_equal,assert_almost_equal, assert_equal
import math

"""
Models the ambient audio scenery with a multivariate
Gaussian distributions. Based on that model we can distinguish 
between the ambient sounds and sounds that are 
unlikely to occur naturally. 

This model allows for correlations between the input values. 

""" 
class MultivariateGaussian(SignalLikelihood):
    def __init__(self): 
        self.mean = None 
        self.var = None
        self.sumSquareDif = None
        self.n = 0 

    def train(self, features):
        """
        Updates the mean and variance of the gaussian model capturing the 
        ambient sound scenery. 
        """
        if self.mean is None:
            # no previous mean or variance exist 
            self.mean = features

            # we need a zero vector with the size of the feature vector
            self.sumSquareDif = np.zeros_like(features)
            self.var = np.zeros_like(features)
            self.n = 1
        else: 
            # previous mean is old_sum / old_n => new_sum = (old_sum * old_n) + new values 
            old_mean = self.mean 
            old_sum = old_mean * self.n 
            new_sum = old_sum + features 
            self.n = self.n + 1
            self.mean = new_sum / self.n 

            # vectorizaed adaption of Knuth's online variance algorithm
            # the original algorithm can be found here:  
            # Donald E. Knuth (1998). The Art of Computer Programming, volume 2: 
            # Seminumerical Algorithms, 3rd edn., p. 232. Boston: Addison-Wesley.

            # update sum of square differences  
            self.sumSquareDif = self.sumSquareDif + (features - old_mean) * (features - self.mean)

            # update variance 
            self.var = self.sumSquareDif / (self.n - 1)

    def calculate_prob(self, features):
        """ 
        Calculates the probability that the signal described by the 
        features is an ambient sound. 
        """ 
        if np.any(self.var == 0): 
            return 0 

        # this is a vectorized version of the pdf of a normal distribution for each frequency amplitude
        # it returns one probability for each of the signal's frequency amplitudes 
        probs = np.exp(-(features-self.mean)**2/(2.*self.var**2)) / (math.sqrt(math.pi * 2.) * self.var)

        # simplificaiton: assumption of independent frequencies => product 
        return np.prod(probs)


class GaussianTests(unittest.TestCase):
    def train(self, data): 
        gaussian = Gaussian() 

        for datum in data: 
            gaussian.train(datum)

        return gaussian

    def checkMean(self, data, expectedMean): 
        gaussian = self.train(data)        
        assert_almost_equal(gaussian.mean, expectedMean)

    def checkVariance(self, data, exptectedVar): 
        gaussian = self.train(data)
        assert_almost_equal(gaussian.var, exptectedVar)


    def test_mean_for_one_feature(self):
        data = [np.array([0.]), np.array([6.]), np.array([10.]), np.array([8.])]
        expectedMean = np.array([6.])

        self.checkMean(data, expectedMean)

    def test_mean_for_multiple_features(self):
        data = [np.array([0., 3.]), np.array([6., 8.]), np.array([10., 4.]), np.array([8., 7.])]
        expectedMean = np.array([6., 5.5])

        self.checkMean(data, expectedMean)

    def test_variance_for_one_feature(self):
        data = [np.array([1.]), np.array([0.]), np.array([2.]), np.array([1.]), np.array([0.])]
        expectedVariance = np.array([0.7])

        self.checkVariance(data, expectedVariance)

    def test_variance_for_one_feature(self):
        data = [np.array([1., 0.]), np.array([0., 2.]), np.array([2., 1.]), np.array([1., 0.]), np.array([0., 1.])]
        expectedVariance = np.array([0.7, 0.7])

        self.checkVariance(data, expectedVariance)

    def test_probability_calculation(self): 
        gaussian = Gaussian() 
        gaussian.mean = np.array([5., 3.])
        gaussian.var = np.array([2., 1.])
        x = np.array([4.,4.])

        expected = 0.0426 
        actual = gaussian.calculate_prob(x)
        assert_almost_equal(actual,expected, decimal=4)

        