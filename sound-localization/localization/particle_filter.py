import random
import itertools
import math
from scipy.spatial import distance
import unittest
from numpy.testing import assert_array_almost_equal,assert_almost_equal, assert_equal


'''
A particle is a possible position of a sound source 
If we have insufficient data to determine the exact 
location of the sound source; multiple particles 
represent each possible location of the sound source. 
Each particle has a probability which represents the 
likelihood that this particle is the correct lcation 
for the sound source. 
'''
class Particle:
	def __init__(self, pos, movement, prob = 1.0): 
		self.pos = pos
		self.movement = movement
		self.prob = prob 
		self.isAlive = True 

	def move(self): 
		self.pos = self.pos + self.movement

	def updateProbability(self, prob): 
		self.prob = prob 

	def determineSurvival(self): 
		''' if the probility that this particle 
		represnts the correct position for the 
		sounds source is low, we destroy the 
		particle'''
		self.isAlive = self.prob >= random.random()
		return self.isAlive


'''
Calculates the probability for a sound source position 
given the sensor data 

This information is used to determine the probability 
of a particle.
'''
class DistanceProbability:
	def calculateDistanceProbability(self, distanceAmplitudePairs): 
		# the pressure of sound is reduced linearly by the distance
		# http://en.wikipedia.org/wiki/Sound_pressure#Distance_law
		# distance law: p ~ 1/r 
		# op (original pressure) / d (distance) =  sp (pressure at sensor)
		# => op1 / d1 = sp1 & op2 / d2 = sp2  [divide both equations]
		# => op1 / d1 * d2 / op2 = sp1 / sp2 [op1 = op2, as it's same audio signal]
		# => d2 / d1 = sp1 / sp2
		# => 0 = sp1 / sp2 - d2 / d1 
		# if the the position is not correct, we need an error term 
		# => e = sp1 / sp2 - d2 / d1
		# => we want to normalize the error term between 0..1, where 1 represents no error 
		# => exp(-|e|) = exp(-| sp1/sp2 - d2/d1 |)

		sumOfProbs = 0. 
		numOfProbs = 0 

		for sensorA, seonsorB in itertools.combinations(distanceAmplitudePairs, repeat=2): 
			d1, sp1 = sensorA
			d2, sp2 = seonsorB
			prob = math.exp(-math.abs( sp1/sp2 - d2/d1 ))

			numOfProbs = numOfProbs + 1
			sumOfProbs = sumOfProbs + prob 

		return sumOfProbs / numOfProbs

''' 
The seonsor grid determines the position probability given 
the sensor data 
''' 
class SensorGrid: 
	def __init__(self, probability_calculator): 
		self._guardians = {}
		self._prob_calc = probability_calculator

	def add(self, guardian): 
		self._guardians[guardian.id] = guardian.detection, guardian.pos

	def remove(self, guardian): 
		del self._guardians[guardian.id]

	def calculatePositionProbability(self, pos): 
		distAmpPairs = []

		for amp,g_pos in self._guardians: 
			dst = distance.euclidean(pos,g_pos)
			distAmpPairs.append(dst, amp)

		return self._prob_calc.calculateDistanceProbability(distAmpPairs)

