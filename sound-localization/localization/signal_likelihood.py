from abc import ABCMeta, abstractmethod

"""
Models the ambient audio scenery. Based on that model 
the object can distinguish between the ambient sounds 
and sounds that are unlikely to occur naturally. 

This class is abstract and both methods need to be 
overriden in the actual implementations. 
""" 
class SignalLikelihood: 
    __metaclass__ = ABCMeta
    
    @abstractmethod
    def train(self, features):
        """
        Use the supplied features to train a model for the ambient 
        audio scenery. This will allow for the calculdation of the 
        probability that a signal is different from the abient sound. 
        """

    @abstractmethod
    def calculate_prob(self, features):
        """ 
        Calculates the probability that the signal described by the 
        features is an ambient sound. 
        """ 
        return 1.0