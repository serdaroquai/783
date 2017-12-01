#Typing Charactheristics

###Notes to self:

Need to be careful about key==13 (Return) flight times being very high between different sessions.

Possible point of failure is having key down at the end of first sentence but the key up at the start of the next. Normally this is not a problem but if that sentence is never saved then it becomes a problem. 

Need to normalize continuous input, and one hot vector the keys (they are discrete)