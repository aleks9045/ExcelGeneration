#!/bin/bash

[ $# -eq 0 ] && { echo "Использование: $0 <файл.log.gz>"; exit 1; }

gzip -cd "$1" | awk '
BEGIN {
    cpu_sum = 0; mem_sum = 0; count = 0;
    cpu_max = 0; mem_max = 0;
}
/System Load/ {
    # Извлекаем CPU и Memory
    cpu = $13; sub(/%/, "", cpu); cpu = cpu + 0;
    mem = $15; mem = mem + 0;

    # Суммируем для среднего
    cpu_sum += cpu;
    mem_sum += mem;
    count++;

    # Определяем максимумы
    if (cpu > cpu_max) cpu_max = cpu;
    if (mem > mem_max) mem_max = mem;
}
END {
    if (count > 0) {
        avg_cpu = cpu_sum / count;
        avg_mem = mem_sum / count;

        printf "\nАнализ логов:\n";
        printf "=================================\n";
        printf "CPU (процессор):\n";
        printf "  Средняя загрузка: %.2f%%\n", avg_cpu;
        printf "  Пиковая загрузка: %.2f%%\n", cpu_max;
        printf "---------------------------------\n";
        printf "Память (RAM):\n";
        printf "  Среднее использование: %.2f MB\n", avg_mem;
        printf "  Максимальное использование: %.2f MB\n", mem_max;
        printf "=================================\n";
    } else {
        print "Не найдено записей о нагрузке системы в логах";
    }
}'
