import { Injectable } from '@angular/core';
import { PositionedSchedule } from '../interface/positioned-schedule.interface';

@Injectable({
  providedIn: 'root',
})
export class ScheduleService {

  static weeks: string[] = [
    'MONDAY',
    'TUESDAY',
    'WEDNESDAY',
    'THURSDAY',
    'FRIDAY',
    'SATURDAY',
    'SUNDAY',
  ];

  getFriendlyWeekday(weekday: string): string | any {
    switch (weekday) {
      case 'MONDAY': return 'segunda-feira';
      case 'TUESDAY': return 'terça-feira';
      case 'WEDNESDAY': return 'quarta-feira';
      case 'THURSDAY': return 'quinta-feira';
      case 'FRIDAY': return 'sexta-feira';
      case 'SATURDAY': return 'sábado';
      case 'SUNDAY': return 'domingo';
      default: return weekday;
    }
  }

  getWeekdayByDictionary(weekday: string): string | any {
    switch (weekday) {
      case 'segunda-feira': return 'MONDAY';
      case 'terça-feira': return 'TUESDAY';
      case 'quarta-feira': return 'WEDNESDAY';
      case 'quinta-feira': return 'THURSDAY';
      case 'sexta-feira': return 'FRIDAY';
      case 'sábado': return 'SATURDAY';
      case 'domingo': return 'SUNDAY';
      default: return weekday;
    }
  }

  // --- Helpers de posicionamento da grade ---

  private toMinutes(time: string): number {
    const [h, m] = time.split(':').map(Number);
    return h * 60 + m;
  }

  getHourRange(schedules: { startTime: string; endTime: string }[]): [number, number] {
    if (schedules.length === 0) return [7, 19];
    let minMinutes = Math.min(...schedules.map(s => this.toMinutes(s.startTime)));
    let maxMinutes = Math.max(...schedules.map(s => this.toMinutes(s.endTime)));
    let startHour = Math.max(0, Math.floor(minMinutes / 60) - 1);
    let endHour = Math.min(24, Math.ceil(maxMinutes / 60) + 1);
    if (endHour - startHour < 8) {
      endHour = Math.min(24, startHour + 8);
    }
    return [startHour, endHour];
  }

  buildPositionedGrid(
    schedules: { id: string; weekday: string; startTime: string; endTime: string }[],
    startHour: number,
    pxPerMinute: number
  ): Record<string, PositionedSchedule[]> {

    const grid: Record<string, PositionedSchedule[]> = {};
    ScheduleService.weeks.forEach(day => grid[day] = []);
    for (const s of schedules) {
      if (!grid[s.weekday]) continue;
      const startMinutes = this.toMinutes(s.startTime) - startHour * 60;
      const endMinutes = this.toMinutes(s.endTime) - startHour * 60;
      grid[s.weekday].push({
        ...s,
        top: startMinutes * pxPerMinute,
        height: Math.max((endMinutes - startMinutes) * pxPerMinute, 28)
      });
    }
    return grid;
  }

  getHourMarks(startHour: number, endHour: number, pxPerMinute: number): { label: string; top: number }[] {
    const marks: { label: string; top: number }[] = [];
    for (let h = startHour; h <= endHour; h++) {
      marks.push({
        label: `${h.toString().padStart(2, '0')}:00`,
        top: (h - startHour) * 60 * pxPerMinute
      });
    }
    return marks;
  }
}
