"""
Digital representation of one forest guardian

A guardian can run in two different modes: 
1) Training: the guardian learns the ambient sounds common to its position
2) Active: the guardian actively scans for unnaturally occuring sounds 

These modes are captured in two behaviour classes that allow us to switch 
at any time. 
"""
class GuardianActiveMode:
    def __init__(self, model): 
        self.model = model 

    def process_audio(self, features): 
        """ 
        Returns the detection probability i.e. whether an unnatural sound occured.  
        """ 
        # the model calculates the probability of the sound being natural 
        return 1. - self.model.CalculateProb(features); 

class GuardianTrainingMode:
    def __init__(self, model): 
        self.model = model 

    def process_audio(self, features): 
        """ 
        We train these features to tune our ambient sound model. 
        The probability for detection is 0 because in training we 
        assume all sounds are natural / ambient. 
        """ 
        self.model.train(features)
        return 0.0; 


class Guardian:
    def __init__(self, model, id, pos):
        """
        The model can either be pre-trained from a previous session 
        or a fresh model. 
        """ 
        # todo: defensive programming
        self.model = model 
        self.id = id 
        self.pos = pos 
        self.detection = 0.6
        self.switch_to_training()
        self._observers = []

    def update(self, features): 
        """ 
        Updates the guardian based on the supplied features 
        """
        self.detection = self.mode.process_audio(features)
        self.broadcast()

    def switch_to_training(self): 
        self.mode = GuardianTrainingMode(self.model)

    def switch_to_active(self): 
        self.mode = GuardianActiveMode(self.model)

    def register(self, observer): 
        self._observers.append(observer)

    def unregister(self, observer): 
        # this will fail if the observer doesn't exist, but in 
        # this case we have bug and failure is a good idea 
        self._observers.remove(observer)

    def broadcast(self): 
        for observer in self._observers: 
            observer.notify(id, self.detection)

    