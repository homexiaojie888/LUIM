import pandas as pd
from statds.no_parametrics import wilconxon, friedman, nemenyi

dataset = pd.read_csv("dataset_time2.csv")
# dataset = pd.read_csv("dataset_memory.csv")
columns = list(dataset.columns)
selected_columns = [columns[1], columns[2]]
# selected_columns = [columns[1], columns[3]]
# alphas = [0.05]
alphas = [0.05, 0.01]
for alpha in alphas:
    statistic, p_value, rejected_value, hypothesis = wilconxon(dataset[selected_columns], alpha)
    print(hypothesis)
    print(f"Statistic {statistic}, Rejected Value {rejected_value}, p−value {p_value}")

    rankings, statistic, p_value, critical_value, hypothesis = friedman(dataset, alpha, minimize=True)
    print(hypothesis)
    print(f"Statistic {statistic}, Rejected Value {rejected_value}, p−value {p_value}")
    print(rankings)
    num_cases = dataset.shape[0]
    ranks_values, critical_distance_nemenyi, figure = nemenyi(rankings, num_cases, alpha)
    print(ranks_values)
    print(critical_distance_nemenyi)
    figure.show()