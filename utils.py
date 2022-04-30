
from os import listdir, path

import pandas as pd


# ------------------------------------ UTILITY FUNCTIONS ----------------------------------------
# Reads input data from a directory - files named 'input_data_{seed}.csv'
# returns (seed, df) tuples
def get_all_inputs(directory):
	input_files = [f for f in listdir(directory) if f.startswith('input_data_')]

	dfs = []

	for f in (input_files):
		seed = f.split('_')[-1].split('.')[0]
		df = pd.read_csv(path.join(directory, f), sep=' ', on_bad_lines='skip', header=None)

		cols = ['priority', 'arrival time']
		for i in range(2, df.shape[1]):
			cols.append('burst ' + str(i))
		df.columns = cols
		df.index.name = "Process"

		dfs.append((seed, df))
		
	return dfs

# Returns a dataframe of all parameters from a file `key=value` pair
def get_params(fname):
		df = pd.read_table(fname, sep='=', on_bad_lines='skip', header=None)
		df.columns = ['param', 'value']
		df.index = df['param']
		df.drop(['param'], axis=1, inplace=True)
		return df

## Returns an array of tuples of all the input params in form of (seed, df), df is a dataframe of the input params
def get_all_input_params(directory):
	input_files = [f for f in listdir(directory) if f.startswith('input_params')]

	dfs = []

	for  f in input_files:
		seed = f.split('_')[-1].split('.')[0]
		dfs = (seed, get_params(path.join(directory, f)))
	return dfs

## Returns an array of tuples of all the simulator params in form of (seed, df), df is a dataframe of the simulator params
def get_all_sim_params(directory):
	input_files = [f for f in listdir(directory) if 'sim_params' in f]

	dfs = []

	for f in input_files:
		df = get_params(path.join(directory, f))
		
		dfs.append(df)
	return dfs



## Gets output data from a directory in form described in `./run.sh`
def get_output_data(directory):
	schedulers = [f for f in listdir(directory)]

	res = {}

	for folder in schedulers:
		res[folder] = []
		for file in listdir(path.join(directory, folder)):
			if file.startswith('output-seed_'):
				seed = file.split('output-seed_')[-1].split('.')[0]
				df = pd.read_table(path.join(directory, folder, file))
				df.index = df['id']
				df.index = ['process_' + str(i) if i > 0 else 'idle_process' for i in df.index]
				res[folder].append((seed, df))

	return res
