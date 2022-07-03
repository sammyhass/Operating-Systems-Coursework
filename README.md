# Operating Systems Coursework

This repo contains the coursework that I undertook for my Operating Systems module which I took as part of my Computer Science Degree in my second year at Sussex Uni.

The task was to implement different CPU scheduling algorithms, and then to come up with a series of experiments to compare and contrast the performance of the different algorithms.

For this coursework I received a mark of 95/100.

## Structure

I'll only list the notable files and folders here, which I personally produced.

- `report.pdf` : The final report for the coursework.
- `os-coursework-1`
  - `src/`
    - `run.sh` - Sets up the experiments and runs them.
    - `report.ipynb` - The jupyter notebook which I used to write the report
    - `experiment.py` - A class which allows me to produce graphs and retrieve information about each of the experiments.
    - `utils.py` - Useful functions that collect the data from the experiments so I can pass it into the experiment class.
    - `*Scheduler.Java` - These files are the implementations of each of the different scheduling algorithms
  - `experiment[n]/` - The parameters and results of each of the experiments
