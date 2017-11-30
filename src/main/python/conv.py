import numpy as np
import pandas as pd
import json

# open data
with open('data.json') as file:
   data = json.load(file)

df =  pd.DataFrame(data).head()
M = df.as_matrix()
