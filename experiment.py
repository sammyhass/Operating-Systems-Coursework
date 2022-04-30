
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd

from utils import (get_all_input_params, get_all_inputs, get_all_sim_params,
                   get_output_data)


class Experiment:

	def __init__(self, directory):
		self.directory = directory
		self.input_data = get_all_inputs(directory + '/inputs')
		self.input_params = get_all_input_params(directory + '/inputs')
		self.sim_params = get_all_sim_params(directory + '/sim_params')
		self.output_data = get_output_data(directory + '/schedulers')

	def get_output(self, scheduler):
		
		return self.output_data[scheduler][:]
	
	def get_output_for_seed(self, scheduler, seed):
		for (s, df) in self.output_data[scheduler]:
			if s == seed:
				return df
		return None
	
	def get_input(self, seed: str):
		return self.input_data[seed]
	
	def get_input_params(self, seed: str):
		return self.input_params[seed]

	def get_sim_params(self):
		return self.sim_params
		
	def get_inputs(self):
		return self.input_data


	def plot_burst_frequences(self, scheduler: str, seed: str):

		df = self.get_output_for_seed(scheduler, seed)
		
		fig, ax = plt.subplots(1, 1, figsize=(16, 6))




		

	## Returns a dataframe of a column for all combinations of schedulers and all seeds
	def get_output_col(self, col: str):
		res = pd.DataFrame()
		for scheduler in self.output_data:
			
			for (seed, df) in self.output_data[scheduler]:
				res[scheduler + '_' + seed] = df[col]
		return res


	def calculate_cpu_utilization_for_output(self, scheduler: str, seed: str):
		df = self.get_output_for_seed(scheduler, seed)

		idle_process = df.loc['idle_process']
		
		total_processes = df.cpuTime.sum()

		return 100 - (total_processes - idle_process.cpuTime) / total_processes

	def plot_gantt(self, scheduler: str, seed: str, sort_by: str = 'startedTime'):
		fig, ax = plt.subplots(1, 1, figsize=(16, 8))

		df = self.get_output_for_seed(scheduler, seed)

		df = df.sort_values(by=sort_by)

		ax.set_xlabel('Time')
		ax.set_ylabel('Process')

		ax.set_title(f"{self.directory}: Gantt chart for {scheduler} with seed {seed} processes sorted by {sort_by}")

		## Plotting the gantt chart
		ax.barh(df.index, df.terminatedTime - df.startedTime,
				left=df.startedTime,
				color='green',
				label='Started -> Terminated',
				alpha=0.5,
		)

		
		## Mark arrival time
		ax.barh(df.index,df.startedTime - df.createdTime, left=df.createdTime, color='red', alpha=1, label="WaitingTime")


		
		
		ax.set_yticks(list(range(df.id.max() + 1)))

		

		
		ax.legend()

	def plot_gantt_all(self, scheduler: str):
		for i, (seed, _) in enumerate(self.input_data):
			self.plot_gantt(scheduler, seed)

	def __str__(self):
		return 'Experiment: ' + self.directory
	
	def plot_cols_for_output(self, scheduler: str, seed: str, cols: list = ['cpuTime', 'startedTime', 'turnaroundTime'], sort_by: str = 'startedTime'):
		fig, ax = plt.subplots(1, 1, figsize=(16, 6))

		df = self.get_output_for_seed(scheduler, seed)[cols]

		df = df.sort_values(by=sort_by)

		df.plot.bar(ax=ax, title=f"{self.directory}: {scheduler} {seed} processes sorted by {sort_by}")
		ax.set_xlabel('Process')
		
		ax.legend()

	
