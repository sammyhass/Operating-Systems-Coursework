#!/bin/bash

## constants
readonly local RUN="java -cp target/os-coursework1-1.0-snapshot.jar"
readonly local SCHEDULERS=(RRScheduler SJFScheduler FcfsScheduler FeedbackRRScheduler IdealSJFScheduler)
readonly local LINE="---------------------------------------------------"

## input parameters
readonly local SEEDS_PER_EXPERIMENT=5
readonly local EXPERIMENT_1_SEEDS=(50 100 200 531 120)
readonly local EXPERIMENT_2_SEEDS=(1234 5678 91011 121314 151617)
readonly local EXPERIMENT_3_SEEDS=(5124 6789 1234 5678 91011)

## input parameters
## (number_of_processes, static_priority, mean_inter_arrival, mean_cpu_burst, mean_io_burst, mean_number_bursts)
readonly local EXPERIMENT_1_INPUT_PARAMS=(50 0 12 10 10 6)
readonly local EXPERIMENT_2_INPUT_PARAMS=(25 0 12 4 24 10)
readonly local EXPERIMENT_3_INPUT_PARAMS=(30 0 20 5 35 4)

## simulator parameters (excluding scheduling algorithm)
# (time_limit, interrupt_time, time_quantum, initial_burst_estimate, alpha_burst_estimate, periodic)
readonly local EXPERIMENT_1_SIMULATOR_PARAMS=(10000 1 5 5 0.5 false)
readonly local EXPERIMENT_2_SIMULATOR_PARAMS=(5000 10 4 14 0.5 false)
readonly local EXPERIMENT_3_SIMULATOR_PARAMS=(5000 10 20 30 0.7 false)

readonly local EXPERIMENT_3_SIMULATOR_PARAMS_IMPROVE=(5000 10 20 15 0.4 false)
### receives input parameters in form of: (file_to_write_to, number_of_processes, static_priority, mean_inter_arrival, mean_cpu_burst, mean_io_burst, mean_number_bursts, seed)
write_input_params() {
	local file_name=$1
	local number_of_processes=$2
	local static_priority=$3
	local mean_inter_arrival=$4
	local mean_cpu_burst=$5
	local mean_io_burst=$6
	local mean_number_bursts=$7
	local seed=$8

	local serialized="numberOfProcesses=$number_of_processes\nstaticPriority=$static_priority\nmeanInterArrival=$mean_inter_arrival\nmeanCpuBurst=$mean_cpu_burst\nmeanIoBurst=$mean_io_burst\nmeanNumberBursts=$mean_number_bursts\nseed=$seed"

	printf $serialized >$file_name

	echo "written input parameters to $file_name"

}

## generate input data using the parameters file. expects the params in form of: (input_parameters_file, output_file)
generate_input_data() {
	local parameters_file=$1
	local data_file_to_write=$2

	$RUN InputGenerator $parameters_file $data_file_to_write

	echo "generated input data in $data_file_to_write"
}

# writes simulator parameters to a file. receives (file_to_write_to, scheduler_to_use, time_limit, interrupt_time, time_quantum, initial_burst_estimate, alpha_burst_estimate, periodic)
write_simulator_parameters() {
	local simulator_parameters_file=$1
	local scheduler_to_use=$2
	local time_limit=$3
	local interrupt_time=$4
	local time_quantum=$5
	local initial_burst_estimate=$6
	local alpha_burst_estimate=$7
	local periodic=$8

	local serialized="scheduler=$scheduler_to_use\ntimeLimit=$time_limit\ninterruptTime=$interrupt_time\ntimeQuantum=$time_quantum\ninitialBurstEstimate=$initial_burst_estimate\nalphaBurstEstimate=$alpha_burst_estimate\nperiodic=$periodic"

	printf $serialized >$simulator_parameters_file

	echo "written simulator parameters to $simulator_parameters_file"

}

## runs the simulator with parameters: (simulator_file, input_data_file, output_path)
run_simulation() {
	local simulator_parameters_file=$1
	local input_data_file=$2
	local output_file=$3

	$RUN Simulator $simulator_parameters_file $output_file $input_data_file
}

## runs a whole experiment, expects a number from 1 to 4 as a parameter
run_experiment() {
	local folder="../experiment$1"
	echo "Running Experiment $1"

	mkdir -p $folder

	local input_params
	local sim_params
	local seeds
	if [ $1 -eq 1 ]; then
		input_params="${EXPERIMENT_1_INPUT_PARAMS[@]}"
		sim_params="${EXPERIMENT_1_SIMULATOR_PARAMS[@]}"
		seeds="${EXPERIMENT_1_SEEDS[@]}"
	elif [ $1 -eq 2 ]; then
		input_params="${EXPERIMENT_2_INPUT_PARAMS[@]}"
		sim_params="${EXPERIMENT_2_SIMULATOR_PARAMS[@]}"
		seeds="${EXPERIMENT_2_SEEDS[@]}"
	elif [ $1 -eq 3 ]; then
		input_params="${EXPERIMENT_3_INPUT_PARAMS[@]}"
		sim_params="${EXPERIMENT_3_SIMULATOR_PARAMS[@]}"
		seeds="${EXPERIMENT_3_SEEDS[@]}"
	elif [ $1 -eq 4 ]; then
		input_params="${EXPERIMENT_3_INPUT_PARAMS[@]}"
		sim_params="${EXPERIMENT_3_SIMULATOR_PARAMS_IMPROVE[@]}"
		seeds="${EXPERIMENT_3_SEEDS[@]}"
	else
		echo "Invalid experiment number"
		exit 1
	fi

	mkdir -p $folder/inputs

	for seed in ${seeds[@]}; do
		local data_file="$folder/inputs/input_data_$seed.in"
		local input_params_file="$folder/inputs/input_params_$seed.prp"
		write_input_params $input_params_file $input_params $seed
		generate_input_data $input_params_file $data_file
	done

	for scheduler in "${SCHEDULERS[@]}"; do
		echo "----------- Running $scheduler -----------"
		mkdir -p $folder/schedulers/$scheduler $folder/sim_params
		local sim_params_file="$folder/sim_params/${scheduler}_sim_params.prp"
		write_simulator_parameters $sim_params_file $scheduler $sim_params

		for seed in ${seeds[@]}; do
			local input_data_file="$folder/inputs/input_data_$seed.in"

			echo "----------- Running with seed: $seed... -----------"
			run_simulation $sim_params_file $input_data_file "$folder/schedulers/$scheduler/output-seed_$seed.out"
			echo "------- Finished Running $scheduler --------"
		done
	done

	echo $LINE
}

main() {
	run_experiment 1
	run_experiment 2
	run_experiment 3

	run_experiment 4 ## experiment 4 is just a copy of experiment 3 with a different simulator parameters file to improve estimation of burst times
}

main
