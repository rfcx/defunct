"""
map 
"""

class Map:
    def __init__(self):
        self.guardians = []
        self.particles = []
        self.prob = 1  

    def add_guardian(self, guardian): 
        self.guardians.append( guardian )

    def get_guardian(self, id): 
    	# Todo: defensive programming
    	# Todo: add check that guardian exists, handle this case more gracefully
    	return filter(lambda guardian: guardian.id == id, self.guardians)[0]

    def add_particle(self, particle): 
    	self.particles.append(particle)

    def move_particles(self): 
    	for particle in self.particles: 
    		particle.move()