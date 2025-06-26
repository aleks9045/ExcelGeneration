#!/bin/bash

# Проверяем наличие файла
if [ $# -eq 0 ]; then
    echo "Использование: $0 <файл.log.gz>"
    exit 1
fi

LOG_FILE=$1

# Основной анализ
gzip -cd "$LOG_FILE" | awk '
BEGIN {
    cpu_sum = 0; mem_sum = 0; count = 0;
    cpu_max = 0; mem_max = 0;
    total_time = 0; time_count = 0;
}
/Request load/ {
    # Извлекаем CPU и Memory
    cpu = $10; sub(/%/, "", cpu); cpu = cpu + 0;
    mem = $12; mem = mem + 0;

    # Суммируем для среднего
    cpu_sum += cpu;
    mem_sum += mem;
    count++;

    # Определяем максимумы
    if (cpu > cpu_max) cpu_max = cpu;
    if (mem > mem_max) mem_max = mem;

}
/Time spent:/ {
    # Извлекаем время выполнения
    time_spent = $NF;
    sub(/s/, "", time_spent);
    total_time += time_spent;
    time_count++;
}
END {
    # Вычисляем средние значения
    avg_cpu = count ? cpu_sum/count : 0;
    avg_mem = count ? mem_sum/count : 0;
    avg_time = time_count ? total_time/time_count : 0;

    # Форматируем вывод
    printf "\nАнализ логов: %s\n", last_time;
    printf "=================================\n";
    printf "CPU (процессор):\n";
    printf "  Средняя загрузка: %.2f%%\n", avg_cpu;
    printf "  Пиковая загрузка: %.2f%%\n", cpu_max;
    printf "---------------------------------\n";
    printf "Память (RAM):\n";
    printf "  Среднее использование: %.2f MB\n", avg_mem;
    printf "  Максимальное использование: %.2f MB\n", mem_max;
    printf "---------------------------------\n";
    printf "Время выполнения:\n";
    printf "  Среднее: %.2f секунд\n", avg_time;
    printf "  Всего запросов: %d\n", time_count;
    printf "=================================\n";
}'

